package com.example.paint_demo;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.List;

public class HelloController {
    @FXML
    Canvas paintCanvas;
    Double startX, startY, endX, endY;
    GraphicsContext gc;
    List<Segment> segmentList = new ArrayList<>();
    private boolean isDragging = false;


    public void initialize() {
        if (paintCanvas != null) {
            gc = paintCanvas.getGraphicsContext2D();
        }
        paintCanvas.addEventHandler(MouseEvent.MOUSE_PRESSED, this::handleMousePressed);
        paintCanvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::handleMouseDragged);
        paintCanvas.addEventHandler(MouseEvent.MOUSE_RELEASED, this::handleMouseReleased);
        paintCanvas.addEventHandler(MouseEvent.MOUSE_MOVED, this::handleMouseMoved); // Adăugați eveniment de "hover"
    }

    @FXML
    private void handleMousePressed(MouseEvent event) {
        double mouseX = event.getX();
        double mouseY = event.getY();
        boolean isHover = false;
        for (Segment segment : segmentList) {
            if (isMouseOverSegmentStart(segment, mouseX, mouseY)) {
                mouseX = segment.getStartX();
                mouseY = segment.getStartY();
                isHover = true;
            } else if (isMouseOverSegmentEnd(segment, mouseX, mouseY)) {
                mouseX = segment.getEndX();
                mouseY = segment.getEndY();
                isHover = true;
            }
        }

        if(isHover){
            startX = mouseX;
            startY = mouseY;
        }else{
            startX = event.getX();
            startY = event.getY();
        }
    }

    @FXML
    private void handleMouseDragged(MouseEvent event) {
        isDragging = true;
        gc.clearRect(0, 0, paintCanvas.getWidth(), paintCanvas.getHeight());
        drawPaint();
        endX = event.getX();
        endY = event.getY();
        drawLine();
    }

    @FXML
    private void handleMouseReleased(MouseEvent event) {
        if (!isDragging) {
            endX = event.getX();
            endY = event.getY();
            segmentSaves();
            drawLine();
        }
        isDragging = false; // Resetăm starea
    }

    @FXML
    private void handleMouseMoved(MouseEvent event) {
        double mouseX = event.getX();
        double mouseY = event.getY();
        gc.clearRect(0, 0, paintCanvas.getWidth(), paintCanvas.getHeight()); // Șterge tot
        drawPaint();
        // Verificați dacă mouse-ul face hover pe capătul de la început al segmentelor
        for (Segment segment : segmentList) {
            if (isMouseOverSegmentStart(segment, mouseX, mouseY)) {
                // Faceți ceva când mouse-ul face hover pe capătul de la început al segmentului
                // De exemplu, puteți afișa un cerc mai mare la coordonatele capătului de la început
                double startX = segment.getStartX();
                double startY = segment.getStartY();
                gc.fillOval(startX - 5, startY - 5, 10,10);// Afișează un cerc mai mare
            } else if (isMouseOverSegmentEnd(segment, mouseX, mouseY)) {
                // Faceți ceva când mouse-ul face hover pe capătul de la sfârșit al segmentului
                // De exemplu, puteți afișa un cerc mai mare la coordonatele capătului de la sfârșit
                double endX = segment.getEndX();
                double endY = segment.getEndY();
                gc.fillOval(endX - 5, endY - 5, 10, 10); // Afișează un cerc mai mare
            }else {
               // gc.clearRect(0, 0, paintCanvas.getWidth(), paintCanvas.getHeight());
                //drawPaint();
            }
        }
    }


    private boolean isMouseOverSegmentStart(Segment segment, double mouseX, double mouseY) {
        double startSegmentX = segment.getStartX();
        double startSegmentY = segment.getStartY();
        double distance = Math.sqrt(Math.pow(mouseX - startSegmentX, 2) + Math.pow(mouseY - startSegmentY, 2));
        double threshold = 5; // Ajustați această valoare pentru zona "hover"

        return distance < threshold;
    }


    private boolean isMouseOverSegmentEnd(Segment segment, double mouseX, double mouseY) {
        double endSegmentX = segment.getEndX();
        double endSegmentY = segment.getEndY();
        double distance = Math.sqrt(Math.pow(mouseX - endSegmentX, 2) + Math.pow(mouseY - endSegmentY, 2));
        double threshold = 5; // Ajustați această valoare pentru zona "hover"

        return distance < threshold;
    }

    private void drawLine() {
        gc.strokeLine(startX, startY, endX, endY);
    }

    private void segmentSaves() {
        Segment segment = new Segment(startX, startY, endX, endY);
        segmentList.add(segment);
        System.out.println(segmentList.size());
    }

    private void drawPaint() {
        for (Segment segment : segmentList) {
            gc.strokeLine(segment.getStartX(), segment.getStartY(), segment.getEndX(), segment.getEndY());
        }
    }
}
