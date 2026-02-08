package com.analistas.gym.model.service;

import com.analistas.gym.model.domain.Socio;
import com.analistas.gym.model.repository.ISocioRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class RecordatorioCuotaService {

    private final ISocioRepository socioRepository;
    private final WhatsAppService whatsappService;

    public RecordatorioCuotaService(
            ISocioRepository socioRepository,
            WhatsAppService whatsappService
    ) {
        this.socioRepository = socioRepository;
        this.whatsappService = whatsappService;
    }

    /**
     * Se ejecuta TODOS LOS DÍAS a las 12:00
     */
    @Scheduled(cron = "0 0 12 * * *")
    // @Scheduled(cron = "0 */1 * * * *")
    public void enviarRecordatoriosCuota() {

        // 👉 mañana
        // Si quisiera cambiar a 2 dias antes, cambio el plusDays por 2, y así
        LocalDate fechaObjetivo = LocalDate.now().plusDays(1);

        List<Socio> socios = socioRepository.findByFechaVencimiento(fechaObjetivo);

        for (Socio socio : socios) {

            if (socio.getTelefono() == null || socio.getTelefono().isBlank()) {
                continue;
            }

            String mensaje = construirMensaje(socio);

            whatsappService.enviarMensajeTexto(
                    socio.getTelefono(),
                    mensaje
            );
        }
    }

    private String construirMensaje(Socio socio) {

        String fecha = socio.getFechaVencimiento()
                .format(DateTimeFormatter.ofPattern("dd/MM"));

        return """
                [Mensaje programado del día %s]

                🏋️‍♂️ CAPTAIN GYM
                Hola %s 👋

                Le informamos que su cuota de la actividad %s
                vence mañana (%s).

                💰 Monto: $ %d

                ¡Lo esperamos!
                """.formatted(
                LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM")),
                socio.getNombreCompleto(),
                socio.getActividad().getNombre(),
                fecha,
                socio.getActividad().getMonto()
        );
    }
}

