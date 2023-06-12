package org.example;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import static java.awt.Color.*;

public class Coleccion extends JFrame {
    static Connection con = DatabaseConnection.getInstance().getConnection();
    private final JPanel panelIzquierdo = new JPanel();
    private final JPanel panelDerecho = new JPanel();
    private final JPanel panelBotones = new JPanel();
    private final JButton moverDerecha = new JButton("-->");
    private final JButton moverIzquierda = new JButton("<--");
    private final JButton borrar = new JButton("Borrar");
    private final JButton crearInforme = new JButton("Informe");
    public static String nombre;
    public static int id;

    public Coleccion(String nombre, int id) {
        Coleccion.id = id;
        createGUI(nombre);
        attachEvents();
        cargarPeliculasIzquierda();
        cargarPeliculasDerecha();
    }


    private void createGUI(String nombre) {
        setTitle("Coleccion de "+ nombre + " ID: " + id);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        Container container = getContentPane();
        container.setLayout(new BorderLayout());
        container.setBackground(darkGray);


        JScrollPane scrollIzquierdo = new JScrollPane(panelIzquierdo);
        panelIzquierdo.setLayout(new BoxLayout(panelIzquierdo, BoxLayout.Y_AXIS));
        panelIzquierdo.setBackground(darkGray);
        scrollIzquierdo.setBorder(new TitledBorder("Me Gustan"));
        TitledBorder borde = (TitledBorder) scrollIzquierdo.getBorder();
        borde.setTitleColor(WHITE);
        scrollIzquierdo.setBorder(borde);
        scrollIzquierdo.setPreferredSize(new Dimension(250, 225));
        scrollIzquierdo.setBackground(darkGray);

        JScrollPane scrollDerecho = new JScrollPane(panelDerecho);
        panelDerecho.setLayout(new BoxLayout(panelDerecho, BoxLayout.Y_AXIS));
        panelDerecho.setBackground(darkGray);
        scrollDerecho.setBorder(new TitledBorder("No me Gustan"));
        TitledBorder borde2 = (TitledBorder) scrollDerecho.getBorder();
        borde2.setTitleColor(WHITE);
        scrollDerecho.setBorder(borde2);
        scrollDerecho.setPreferredSize(new Dimension(250, 225));
        scrollDerecho.setBackground(darkGray);

        panelBotones.setLayout(new BoxLayout(panelBotones, BoxLayout.Y_AXIS));
        panelBotones.setPreferredSize(new Dimension(150, 225));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(50, 10, 50, 10));
        panelBotones.setBackground(darkGray);

        panelBotones.add(Box.createVerticalGlue());

        moverDerecha.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelBotones.add(moverDerecha);
        moverDerecha.setBackground(gray);
        moverDerecha.setForeground(WHITE);
        moverDerecha.setRolloverEnabled(false);

        panelBotones.add(Box.createRigidArea(new Dimension(0, 10)));

        moverIzquierda.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelBotones.add(moverIzquierda);
        moverIzquierda.setBackground(gray);
        moverIzquierda.setForeground(WHITE);
        moverIzquierda.setRolloverEnabled(false);

        panelBotones.add(Box.createRigidArea(new Dimension(0, 10)));

        borrar.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelBotones.add(borrar);
        borrar.setBackground(gray);
        borrar.setForeground(WHITE);
        borrar.setRolloverEnabled(false);

        panelBotones.add(Box.createRigidArea(new Dimension(0, 10)));

        crearInforme.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelBotones.add(crearInforme);
        crearInforme.setBackground(gray);
        crearInforme.setForeground(WHITE);
        crearInforme.setRolloverEnabled(false);

        panelBotones.add(Box.createVerticalGlue());


        container.add(scrollIzquierdo, BorderLayout.WEST);
        container.add(panelBotones, BorderLayout.CENTER);
        container.add(scrollDerecho, BorderLayout.EAST);

        pack();
        setLocationRelativeTo(null);
    }


    private void attachEvents() {
        moverIzquierda.addActionListener(e -> {
            Component[] components = panelDerecho.getComponents();
            for (Component component : components) {
                if (component instanceof JCheckBox) {
                    JCheckBox checkBox = (JCheckBox) component;
                    if (checkBox.isSelected()) {
                        String pelicula = checkBox.getText();
                        cambiarMeGusta(pelicula, 1, 0);
                    }
                }
            }
            panelDerecho.removeAll();
            cargarPeliculasDerecha();
            panelDerecho.revalidate();
            panelIzquierdo.revalidate();
            panelDerecho.repaint();
            panelIzquierdo.repaint();
            panelDerecho.setVisible(false);
            panelDerecho.setVisible(true);
        });
        
        moverDerecha.addActionListener(e -> {
            Component[] components = panelIzquierdo.getComponents();
            for (Component component : components) {
                if (component instanceof JCheckBox) {
                    JCheckBox checkBox = (JCheckBox) component;
                    if (checkBox.isSelected()) {
                        String pelicula = checkBox.getText();
                        cambiarMeGusta(pelicula, 0, 1);
                    }
                }
            }
            panelIzquierdo.removeAll();
            cargarPeliculasIzquierda();
            panelIzquierdo.revalidate();
            panelDerecho.revalidate();
            panelDerecho.repaint();
            panelIzquierdo.repaint();
            panelIzquierdo.setVisible(false);
            panelIzquierdo.setVisible(true);
        });

        crearInforme.addActionListener(e -> {
            int cantidadPeliculas = obtenerCantidadPeliculas();

            if (cantidadPeliculas == 0) {
                JOptionPane.showMessageDialog(this, "No hay películas en la colección.", "Información", JOptionPane.INFORMATION_MESSAGE);
                return;
            }else{
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Guardar informe");
            int userSelection = fileChooser.showSaveDialog(this);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                String path = fileToSave.getAbsolutePath();

                List<String> meGusta = obtenerPeliculasIzquierda();
                List<String> noMeGusta = obtenerPeliculasDerecha();

                try (PrintWriter writer = new PrintWriter(fileToSave)) {
                    StringBuilder reportBuilder = new StringBuilder();

                    reportBuilder.append("-----------------------------------------\nInforme de películas:\n-----------------------------------------\n");

                    reportBuilder.append("-----------------------------------------\nPeliculas que SI me gustan:\n-----------------------------------------\n");
                    for (String pelicula : meGusta) {
                        reportBuilder.append("- ").append(pelicula).append("\n");
                    }

                    reportBuilder.append("-----------------------------------------\n");

                    reportBuilder.append("-----------------------------------------\nPeliculas que NO me gustan:\n-----------------------------------------\n");
                    for (String pelicula : noMeGusta) {
                        reportBuilder.append("- ").append(pelicula).append("\n");
                    }

                    reportBuilder.append("-----------------------------------------\n");

                    writer.println(reportBuilder.toString());
                    writer.flush();
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                }
            }
        }});

        borrar.addActionListener(e -> {
            Component[] components = panelDerecho.getComponents();
            for (Component component : components) {
                if (component instanceof JCheckBox) {
                    JCheckBox checkBox = (JCheckBox) component;
                    if (checkBox.isSelected()) {
                        String pelicula = checkBox.getText();
                        eliminarPelicula(pelicula);
                        panelIzquierdo.remove(checkBox);
                        panelDerecho.remove(checkBox);
                    }
                }
            }

            components = panelIzquierdo.getComponents();
            for (Component component : components) {
                if (component instanceof JCheckBox) {
                    JCheckBox checkBox = (JCheckBox) component;
                    if (checkBox.isSelected()) {
                        String pelicula = checkBox.getText();
                        eliminarPelicula(pelicula);
                        panelIzquierdo.remove(checkBox);
                        panelDerecho.remove(checkBox);
                    }
                }
            }

            panelIzquierdo.revalidate();
            panelIzquierdo.repaint();
            panelDerecho.revalidate();
            panelDerecho.repaint();
        });


    }
    private int obtenerCantidadPeliculas() {
        int cantidad = 0;
        String sql = "SELECT COUNT(*) FROM Coleccion WHERE user_id = ?";

        try (PreparedStatement statement = con.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                cantidad = resultSet.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return cantidad;
    }

    private java.util.List<String> obtenerPeliculasIzquierda() {
        List<String> peliculas = new ArrayList<>();
        String sql = "SELECT Nombre FROM Coleccion WHERE MeGusta = 0 AND user_id = " + id;
        try (PreparedStatement statement = con.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String pelicula = resultSet.getString("Nombre");
                peliculas.add(pelicula);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return peliculas;
    }
    private java.util.List<String> obtenerPeliculasDerecha() {
        List<String> peliculas = new ArrayList<>();
        String sql = "SELECT Nombre FROM Coleccion WHERE MeGusta = 1 AND user_id = " + id;
        try (PreparedStatement statement = con.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String pelicula = resultSet.getString("Nombre");
                peliculas.add(pelicula);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return peliculas;
    }
    private void cargarPeliculasIzquierda() {
        List<String> peliculas = obtenerPeliculasIzquierda();
        for (String pelicula : peliculas) {
            JCheckBox checkBox = new JCheckBox(pelicula);
            checkBox.setBackground(darkGray);
            checkBox.setForeground(white);
            panelIzquierdo.add(checkBox);
        }
    }
    private void cargarPeliculasDerecha() {
        List<String> peliculas = obtenerPeliculasDerecha();
        for (String pelicula : peliculas) {
            JCheckBox checkBox = new JCheckBox(pelicula);
            checkBox.setBackground(darkGray);
            checkBox.setForeground(white);
            panelDerecho.add(checkBox);
        }
    }
    private void cambiarMeGusta(String pelicula, int valorActual, int nuevoValor) {
        String sql = "UPDATE Coleccion SET MeGusta = ? WHERE Nombre = ? AND MeGusta = ? AND user_id = ?";
        try (PreparedStatement statement = con.prepareStatement(sql)) {
            statement.setInt(1, nuevoValor);
            statement.setString(2, pelicula);
            statement.setInt(3, valorActual);
            statement.setInt(4, id);
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void eliminarPelicula(String pelicula) {
        String sql = "DELETE FROM Coleccion WHERE Nombre = ? AND user_id = ?";
        try (PreparedStatement statement = con.prepareStatement(sql)) {
            statement.setString(1, pelicula);
            statement.setInt(2, id);
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Coleccion(nombre, id).setVisible(true));
    }
}

