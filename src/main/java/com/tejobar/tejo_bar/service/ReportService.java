package com.tejobar.tejo_bar.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.tejobar.tejo_bar.model.Equipo;
import com.tejobar.tejo_bar.model.Jugador;
import com.tejobar.tejo_bar.model.Partido;
import com.tejobar.tejo_bar.model.Producto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ReportService {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private JugadorService jugadorService;

    @Autowired
    private EquipoService equipoService;

    @Autowired
    private PartidoService partidoService;

    public byte[] generateGeneralReport() throws DocumentException, IOException {
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PdfWriter.getInstance(document, out);
        document.open();

        // Título
        Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.BLACK);
        Paragraph title = new Paragraph("Reporte General - TejoBar", fontTitle);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph(" "));

        // Sección Productos
        addSectionTitle(document, "Productos Registrados");
        List<Producto> productos = productoService.findAll();
        PdfPTable tableProductos = new PdfPTable(4);
        tableProductos.setWidthPercentage(100);
        addTableHeader(tableProductos, "ID", "Nombre", "Precio", "Stock");
        for (Producto p : productos) {
            tableProductos.addCell(String.valueOf(p.getIdProducto()));
            tableProductos.addCell(p.getNombre());
            tableProductos.addCell("$" + p.getPrecio());
            tableProductos.addCell(String.valueOf(p.getStock()));
        }
        document.add(tableProductos);
        document.add(new Paragraph(" "));

        // Sección Jugadores
        addSectionTitle(document, "Jugadores Registrados");
        List<Jugador> jugadores = jugadorService.findAll();
        PdfPTable tableJugadores = new PdfPTable(3);
        tableJugadores.setWidthPercentage(100);
        addTableHeader(tableJugadores, "ID", "Nombre", "Estado");
        for (Jugador j : jugadores) {
            tableJugadores.addCell(String.valueOf(j.getIdPersona()));
            tableJugadores.addCell(j.getNombre()); // Heredado de Persona
            tableJugadores.addCell(Boolean.TRUE.equals(j.getEstado()) ? "Activo" : "Inactivo");
        }
        document.add(tableJugadores);
        document.add(new Paragraph(" "));

        // Sección Equipos
        addSectionTitle(document, "Equipos Registrados");
        List<Equipo> equipos = equipoService.findAll();
        PdfPTable tableEquipos = new PdfPTable(2);
        tableEquipos.setWidthPercentage(100);
        addTableHeader(tableEquipos, "ID", "Nombre");
        for (Equipo e : equipos) {
            tableEquipos.addCell(String.valueOf(e.getIdEquipo()));
            tableEquipos.addCell(e.getNombreEquipo());
        }
        document.add(tableEquipos);
        document.add(new Paragraph(" "));

        // Sección Partidos
        addSectionTitle(document, "Partidos Registrados");
        List<Partido> partidos = partidoService.findAll();
        PdfPTable tablePartidos = new PdfPTable(5);
        tablePartidos.setWidthPercentage(100);
        addTableHeader(tablePartidos, "ID", "Fecha", "Equipo 1", "Equipo 2", "Cancha");
        for (Partido p : partidos) {
            tablePartidos.addCell(String.valueOf(p.getIdPartido()));
            tablePartidos.addCell(p.getFecha() != null ? p.getFecha().toString() : "N/A");
            tablePartidos.addCell(p.getEquipo1() != null ? p.getEquipo1().getNombreEquipo() : "N/A");
            tablePartidos.addCell(p.getEquipo2() != null ? p.getEquipo2().getNombreEquipo() : "N/A");
            tablePartidos.addCell(p.getCancha() != null ? String.valueOf(p.getCancha().getIdCancha()) : "N/A");
        }
        document.add(tablePartidos);

        document.close();
        return out.toByteArray();
    }

    private void addSectionTitle(Document document, String text) throws DocumentException {
        Font fontSection = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.DARK_GRAY);
        Paragraph sectionTitle = new Paragraph(text, fontSection);
        sectionTitle.setSpacingAfter(10);
        document.add(sectionTitle);
    }

    private void addTableHeader(PdfPTable table, String... headers) {
        for (String header : headers) {
            PdfPCell headerCell = new PdfPCell();
            headerCell.setBackgroundColor(Color.LIGHT_GRAY);
            headerCell.setPadding(5);
            headerCell.setPhrase(new Phrase(header));
            table.addCell(headerCell);
        }
    }
}
