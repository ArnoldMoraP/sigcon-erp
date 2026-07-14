package dsw.msrrhh.service;

import dsw.msrrhh.model.*;
import dsw.msrrhh.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlanillaService {

    private final PlanillaRepository planillaRepository;
    private final PlanillaDetalleRepository detalleRepository;
    private final PagoPlanillaRepository pagoRepository;
    private final EmpleadoRepository empleadoRepository;
    private final IncidenciaPersonalRepository incidenciaRepository;

    /** Descuento de ley (AFP/ONP aproximado) sobre el sueldo base. */
    private static final BigDecimal DESCUENTO_LEY = new BigDecimal("0.13");
    /** Descuento por cada incidencia abierta del periodo. */
    private static final BigDecimal DESCUENTO_INCIDENCIA = new BigDecimal("20.00");

    private String generarCodigo() {
        int max = 0;
        for (Planilla p : planillaRepository.findAll()) {
            String c = p.getCodigo();
            if (c == null || !c.startsWith("PLA-")) continue;
            try {
                max = Math.max(max, Integer.parseInt(c.substring(4).trim()));
            } catch (NumberFormatException ignored) { }
        }
        return String.format("PLA-%04d", max + 1);
    }

    @Transactional(readOnly = true)
    public List<Planilla> listar() { return planillaRepository.findAllByOrderByIdDesc(); }

    @Transactional(readOnly = true)
    public Planilla obtenerPorId(Long id) {
        return planillaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Planilla no encontrada: " + id));
    }

    /** Devuelve la planilla + su detalle (una fila por empleado). */
    @Transactional(readOnly = true)
    public Map<String, Object> obtenerConDetalle(Long id) {
        Planilla p = obtenerPorId(id);
        Map<String, Object> res = new LinkedHashMap<>();
        res.put("planilla", p);
        res.put("detalle", detalleRepository.findByPlanillaId(id));
        return res;
    }

    /**
     * Genera la planilla del periodo 'YYYY-MM' con todos los empleados ACTIVOS.
     * neto = sueldo_base + bonificaciones - descuentos
     */
    @Transactional
    public Map<String, Object> generar(String periodo, Long usuarioId) {
        if (periodo == null || !periodo.matches("\\d{4}-\\d{2}"))
            throw new IllegalArgumentException("El periodo debe tener el formato YYYY-MM (ej: 2026-07)");

        if (planillaRepository.existsByPeriodo(periodo))
            throw new IllegalArgumentException("Ya existe una planilla generada para el periodo " + periodo);

        List<Empleado> activos = empleadoRepository.findByEstado("ACTIVO");
        if (activos.isEmpty())
            throw new IllegalArgumentException("No hay empleados ACTIVOS para generar la planilla");

        Planilla planilla = new Planilla();
        planilla.setCodigo(generarCodigo());
        planilla.setPeriodo(periodo);
        planilla.setFechaGeneracion(LocalDate.now());
        planilla.setEstado("GENERADA");
        planilla.setUsuarioId(usuarioId);
        planilla.setTotalIngresos(BigDecimal.ZERO);
        planilla.setTotalDescuentos(BigDecimal.ZERO);
        planilla.setTotalNeto(BigDecimal.ZERO);
        Planilla guardada = planillaRepository.save(planilla);

        BigDecimal totalIngresos   = BigDecimal.ZERO;
        BigDecimal totalDescuentos = BigDecimal.ZERO;
        List<PlanillaDetalle> detalles = new ArrayList<>();

        for (Empleado e : activos) {
            BigDecimal base = (e.getSalario() == null ? BigDecimal.ZERO : e.getSalario());

            long incidencias = incidenciaRepository.findByEmpleadoIdOrderByFechaDesc(e.getId()).stream()
                    .filter(i -> i.getFecha() != null
                              && String.format("%04d-%02d", i.getFecha().getYear(), i.getFecha().getMonthValue()).equals(periodo)
                              && "ABIERTA".equalsIgnoreCase(i.getEstado()))
                    .count();

            BigDecimal descuentos = base.multiply(DESCUENTO_LEY)
                    .add(DESCUENTO_INCIDENCIA.multiply(BigDecimal.valueOf(incidencias)))
                    .setScale(2, RoundingMode.HALF_UP);

            BigDecimal bonificaciones = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
            BigDecimal neto = base.add(bonificaciones).subtract(descuentos).setScale(2, RoundingMode.HALF_UP);

            PlanillaDetalle d = new PlanillaDetalle();
            d.setPlanillaId(guardada.getId());
            d.setEmpleadoId(e.getId());
            d.setSueldoBase(base.setScale(2, RoundingMode.HALF_UP));
            d.setBonificaciones(bonificaciones);
            d.setDescuentos(descuentos);
            d.setNeto(neto);
            detalles.add(d);

            totalIngresos   = totalIngresos.add(base);
            totalDescuentos = totalDescuentos.add(descuentos);
        }

        detalleRepository.saveAll(detalles);

        guardada.setTotalIngresos(totalIngresos.setScale(2, RoundingMode.HALF_UP));
        guardada.setTotalDescuentos(totalDescuentos.setScale(2, RoundingMode.HALF_UP));
        guardada.setTotalNeto(totalIngresos.subtract(totalDescuentos).setScale(2, RoundingMode.HALF_UP));
        planillaRepository.save(guardada);

        log.info("Planilla {} generada para el periodo {} ({} empleados)",
                guardada.getCodigo(), periodo, detalles.size());

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("planilla", guardada);
        res.put("detalle", detalles);
        return res;
    }

    @Transactional
    public Planilla aprobar(Long id) {
        Planilla p = obtenerPorId(id);
        if (!"GENERADA".equalsIgnoreCase(p.getEstado()))
            throw new IllegalArgumentException("Solo se puede aprobar una planilla GENERADA. Estado actual: " + p.getEstado());
        p.setEstado("APROBADA");
        return planillaRepository.save(p);
    }

    /** Registra el pago de una planilla APROBADA y la marca como PAGADA. */
    @Transactional
    public PagoPlanilla pagar(Long id, String metodoPago) {
        Planilla p = obtenerPorId(id);
        if (!"APROBADA".equalsIgnoreCase(p.getEstado()))
            throw new IllegalArgumentException("Solo se pueden pagar planillas APROBADAS. Estado actual: " + p.getEstado());

        PagoPlanilla pago = new PagoPlanilla();
        pago.setPlanillaId(p.getId());
        pago.setMonto(p.getTotalNeto());
        pago.setFechaPago(LocalDate.now());
        pago.setMetodoPago(metodoPago != null ? metodoPago : "TRANSFERENCIA");
        pago.setEstado("PAGADO");
        PagoPlanilla guardado = pagoRepository.save(pago);

        p.setEstado("PAGADA");
        planillaRepository.save(p);

        log.info("Planilla {} pagada por {}", p.getCodigo(), pago.getMonto());
        return guardado;
    }

    @Transactional(readOnly = true)
    public List<PagoPlanilla> listarPagos() { return pagoRepository.findAllByOrderByIdDesc(); }
}
