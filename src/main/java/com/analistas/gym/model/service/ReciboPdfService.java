package com.analistas.gym.model.service;

import com.analistas.gym.model.domain.Socio;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.LineSeparator;

// @Service
// public class ReciboPdfService {

//     public byte[] generarReciboPdf(Socio socio, Integer monto) {

//         try {
//             Document document = new Document(PageSize.A4);
//             ByteArrayOutputStream out = new ByteArrayOutputStream();
//             PdfWriter.getInstance(document, out);

//             document.open();

//             // ================= LOGO =================
//             InputStream logoStream = new ClassPathResource("static/img/captain-gym.png").getInputStream();

//             Image logo = Image.getInstance(logoStream.readAllBytes());
//             logo.scaleAbsolute(180, 180);
//             logo.setAlignment(Image.LEFT);

//             document.add(logo);

//             // ================= FUENTES =================
//             Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
//             Font boldFont = new Font(Font.HELVETICA, 10, Font.BOLD);
//             Font normal = new Font(Font.HELVETICA, 9);
//             Font small = new Font(Font.HELVETICA, 8);

//             // ================= CABECERA =================
//             Paragraph title = new Paragraph("CAPTAIN GYM", titleFont);
//             title.setAlignment(Element.ALIGN_CENTER);
//             document.add(title);

//             document.add(new LineSeparator());

//             String nroRecibo = generarNumeroRecibo(socio);

//             PdfPTable header = new PdfPTable(2);
//             header.setWidthPercentage(100);
//             header.setSpacingBefore(10);

//             header.addCell(cell("Fecha:", boldFont));
//             header.addCell(cell(
//                     LocalDateTime.now()
//                             .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
//                     normal));

//             header.addCell(cell("Recibo N°:", boldFont));
//             header.addCell(cell(nroRecibo, normal));

//             header.addCell(cell("Socio:", boldFont));
//             header.addCell(cell(socio.getNombreCompleto(), normal));

//             header.addCell(cell("DNI:", boldFont));
//             header.addCell(cell(socio.getDni(), normal));

//             header.addCell(cell("Actividad:", boldFont));
//             header.addCell(cell(
//                     socio.getActividad() != null
//                             ? socio.getActividad().getNombre()
//                             : "Sin actividad",
//                     normal));

//             document.add(header);

//             document.add(new LineSeparator());

//             // ================= DETALLE =================
//             Paragraph detalle = new Paragraph(
//                     "Pago de cuota mensual", normal);
//             detalle.setSpacingBefore(10);
//             document.add(detalle);

//             PdfPTable tabla = new PdfPTable(2);
//             tabla.setWidthPercentage(100);
//             tabla.setSpacingBefore(10);

//             tabla.addCell(cell("Detalle", boldFont));
//             tabla.addCell(cell("Monto", boldFont));

//             tabla.addCell(cell(
//                     socio.getActividad().getNombre()
//                             + " | Pago mensual",
//                     normal));

//             tabla.addCell(cell("$ " + monto, normal));

//             document.add(tabla);

//             document.add(new LineSeparator());

//             // ================= TOTAL =================
//             Paragraph total = new Paragraph(
//                     "TOTAL: $ " + monto,
//                     new Font(Font.HELVETICA, 11, Font.BOLD));
//             total.setAlignment(Element.ALIGN_RIGHT);
//             total.setSpacingBefore(10);
//             document.add(total);

//             Paragraph nota = new Paragraph(
//                     "NO VÁLIDO COMO FACTURA",
//                     small);
//             nota.setAlignment(Element.ALIGN_RIGHT);
//             document.add(nota);

//             document.close();

//             return out.toByteArray();

//         } catch (Exception e) {
//             throw new RuntimeException("Error generando PDF", e);
//         }
//     }

//     // ================= HELPERS =================
//     private PdfPCell cell(String text, Font font) {
//         PdfPCell cell = new PdfPCell(new Phrase(text, font));
//         cell.setBorder(Rectangle.NO_BORDER);
//         return cell;
//     }

//     private String generarNumeroRecibo(Socio socio) {
//         String fecha = LocalDateTime.now()
//                 .format(DateTimeFormatter.ofPattern("ddMMyyyy"));

//         return "REC-" + fecha + "-" + String.format("%06d", socio.getId());
//     }
// }
