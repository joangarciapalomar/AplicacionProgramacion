package org.example;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import static java.awt.Color.*;

public class App extends JFrame {
    static Connection con = DatabaseConnection.getInstance().getConnection();
    private final JTextField texto = new JTextField();
    private final JPanel panelAnyadir = new JPanel();
    private final JPanel panelLista = new JPanel();
    private final JPanel panelPeliculas = new JPanel();
    private final JLabel anyadirPelicula = new JLabel("Añadir:");
    private final JButton anyadir = new JButton("-> Añadir");
    private final JButton coleccion = new JButton("Coleccion");
    private final JButton gusta = new JButton("☺");
    private final JButton noGusta = new JButton("\uD83D\uDE1E");
    private final JRadioButton prio = new JRadioButton("Priorizar");
    private final JRadioButton noPrio = new JRadioButton("No priorizar");
    private final ButtonGroup radioButtonGroup = new ButtonGroup();
    public static String nombre;
    public static int id;

    public App(String nombre, int id) {
        App.nombre = nombre;
        App.id = id;
        createGUI(nombre);
        attachEvents();
        cargarPeliculas();
    }

    private void createGUI(String nombre) {
        setTitle("Peliculas de " + nombre + " ID: " + id);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Container container1 = getContentPane();
        container1.setLayout(new BoxLayout(container1, BoxLayout.X_AXIS));
        container1.setPreferredSize(new Dimension(550, 225));
        container1.setBackground(darkGray);

        panelAnyadir.setBorder(new TitledBorder("Añadir Peliculas"));
        TitledBorder borde = (TitledBorder) panelAnyadir.getBorder();
        borde.setTitleColor(WHITE);
        panelAnyadir.setBorder(borde);
        panelAnyadir.setLayout(new BoxLayout(panelAnyadir, BoxLayout.Y_AXIS));
        panelAnyadir.setPreferredSize(new Dimension(220, 225));
        panelAnyadir.setBackground(darkGray);
        container1.add(panelAnyadir);


        JPanel panelAnyadirPel = new JPanel();
        panelAnyadirPel.setBackground(darkGray);
        panelAnyadirPel.add(anyadirPelicula);
        anyadirPelicula.setBorder(new EmptyBorder(0, 10, 0, 0));
        anyadirPelicula.setForeground(WHITE);
        panelAnyadirPel.add(texto);
        texto.setPreferredSize(new Dimension(100, 20));
        texto.setBorder(BorderFactory.createLineBorder(darkGray, 1));
        panelAnyadir.add(panelAnyadirPel);

        JPanel panelAnyadirRadios = new JPanel();
        panelAnyadirRadios.setBackground(DARK_GRAY);
        panelAnyadirRadios.setLayout(new FlowLayout(FlowLayout.LEFT));
        panelAnyadirRadios.add(prio);
        prio.setBackground(darkGray);
        prio.setForeground(white);
        panelAnyadirRadios.add(noPrio);
        noPrio.setBackground(darkGray);
        noPrio.setForeground(white);
        panelAnyadirRadios.setBorder(new EmptyBorder(0, 18, 0, 0));
        panelAnyadir.add(panelAnyadirRadios);

        JPanel panelAnyadirB = new JPanel();
        panelAnyadirB.setBackground(darkGray);
        panelAnyadirB.add(coleccion);
        coleccion.setBackground(Color.gray);
        coleccion.setForeground(Color.WHITE);
        coleccion.setRolloverEnabled(false);
        panelAnyadirB.add(anyadir);
        anyadir.setBackground(Color.gray);
        anyadir.setForeground(Color.WHITE);
        anyadir.setRolloverEnabled(false);
        panelAnyadir.add(panelAnyadirB);

        container1.add(panelLista);
        panelLista.setLayout(new BorderLayout());
        panelLista.setBackground(darkGray);
        panelPeliculas.setLayout(new BoxLayout(panelPeliculas, BoxLayout.Y_AXIS));
        panelLista.setBorder(BorderFactory.createTitledBorder("Lista de Peliculas"));

        JPanel panelListaGustas = new JPanel();
        panelListaGustas.setBackground(darkGray);
        panelListaGustas.setForeground(white);
        panelLista.add(panelListaGustas, BorderLayout.SOUTH);

        panelListaGustas.add(gusta);
        gusta.setBackground(Color.gray);
        gusta.setForeground(GREEN);
        gusta.setRolloverEnabled(false);
        panelListaGustas.add(noGusta);
        noGusta.setBackground(Color.gray);
        noGusta.setForeground(RED);
        noGusta.setRolloverEnabled(false);

        JScrollPane scrollPane = new JScrollPane(panelPeliculas);
        panelPeliculas.setBackground(darkGray);
        scrollPane.setPreferredSize(new Dimension(400, 500));
        scrollPane.setBackground(darkGray);
        panelLista.add(scrollPane, BorderLayout.CENTER);
        panelLista.setBackground(darkGray);
        TitledBorder borde2 = (TitledBorder) panelLista.getBorder();
        borde2.setTitleColor(WHITE);
        panelLista.setBorder(borde2);

        radioButtonGroup.add(prio);
        radioButtonGroup.add(noPrio);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void attachEvents() {
        anyadir.addActionListener(e -> {
            String pelicula = texto.getText();
            if (existePelicula(pelicula) || Objects.equals(pelicula, "")) {
                JOptionPane.showMessageDialog(App.this, "La película ya existe en la lista o está vacía. \nIntentalo de nuevo", "Película duplicada o vacía", JOptionPane.WARNING_MESSAGE);
            } else {
                insertarPelicula(pelicula);
                JCheckBox checkBox = new JCheckBox(pelicula);
                checkBox.setBackground(darkGray);
                checkBox.setForeground(white);
                if (prio.isSelected()) {
                    panelPeliculas.add(checkBox, 0);
                } else {
                    panelPeliculas.add(checkBox);
                }
                panelPeliculas.revalidate();
                panelPeliculas.repaint();
                texto.setText("");
            }
        });

        gusta.addActionListener(e -> {
            Component[] components = panelPeliculas.getComponents();
            for (Component component : components) {
                if (component instanceof JCheckBox) {
                    JCheckBox checkBox = (JCheckBox) component;
                    if (checkBox.isSelected()) {
                        String pelicula = checkBox.getText();
                        if (existePeliculaColeccion(pelicula)){
                            JOptionPane.showMessageDialog(App.this, "La película ya existe en tu coleccion.", "Película duplicada", JOptionPane.WARNING_MESSAGE);
                        }else {
                            agregarPeliculaGusta(pelicula);
                            panelPeliculas.remove(checkBox);
                        }
                    }
                }
            }
            panelPeliculas.revalidate();
            panelPeliculas.repaint();
        });

        noGusta.addActionListener(e -> {
            Component[] components = panelPeliculas.getComponents();
            for (Component component : components) {
                if (component instanceof JCheckBox) {
                    JCheckBox checkBox = (JCheckBox) component;
                    if (checkBox.isSelected()) {
                        String pelicula = checkBox.getText();
                        if (existePeliculaColeccion(pelicula)){
                            JOptionPane.showMessageDialog(App.this, "La película ya existe en tu coleccion.", "Película duplicada", JOptionPane.WARNING_MESSAGE);
                        }else {
                            agregarPeliculaNoGusta(pelicula);
                            panelPeliculas.remove(checkBox);
                        }
                    }
                }
            }
            panelPeliculas.revalidate();
            panelPeliculas.repaint();
        });

        coleccion.addActionListener(e -> {
            JOptionPane.showMessageDialog(App.this, "Para guardar los cambios cierra la ventana de Coleccion. \nAl volver a entrar se muestra el resultado", "Coleccion", JOptionPane.INFORMATION_MESSAGE);
            Coleccion coleccionWindow = new Coleccion(nombre, id);
            coleccionWindow.setVisible(true);
        });
    }


    private void cargarPeliculas() {
        List<String> peliculas = obtenerPeliculas();
        for (String pelicula : peliculas) {
            JCheckBox checkBox = new JCheckBox(pelicula);
            checkBox.setBackground(darkGray);
            checkBox.setForeground(white);
            panelPeliculas.add(checkBox);
        }
    }

    private void insertarPelicula(String pelicula) {
        if (existePelicula(pelicula)) {
            JOptionPane.showMessageDialog(App.this, "La película ya existe en la lista.", "Película duplicada", JOptionPane.WARNING_MESSAGE);
            JOptionPane.showMessageDialog(App.this, "La película ya existe en la lista.", "Película duplicada", JOptionPane.WARNING_MESSAGE);
        } else {
            String sql = "INSERT INTO Peliculas (Nombre) VALUES (?)";
            try (PreparedStatement statement = con.prepareStatement(sql)) {
                statement.setString(1, pelicula);
                statement.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean existePelicula(String pelicula) {
        String sql = "SELECT COUNT(*) FROM Peliculas WHERE Nombre = ?";
        try (PreparedStatement statement = con.prepareStatement(sql)) {
            statement.setString(1, pelicula);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    private boolean existePeliculaColeccion(String pelicula) {
        String sql = "SELECT COUNT(*) FROM Coleccion WHERE Nombre = ? AND user_id = " + id;
        try (PreparedStatement statement = con.prepareStatement(sql)) {
            statement.setString(1, pelicula);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private List<String> obtenerPeliculas() {
        List<String> peliculas = new ArrayList<>();
        String sql = "SELECT Nombre FROM Peliculas";
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

    private void agregarPeliculaGusta(String pelicula) {
        String sql = "INSERT INTO Coleccion (Nombre, MeGusta, user_id, pelicula_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = con.prepareStatement(sql)) {
            statement.setString(1, pelicula);
            statement.setInt(2, 0);
            statement.setInt(3, id);

            int peliculaId = obtenerIdPelicula(pelicula);
            statement.setInt(4, peliculaId);

            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void agregarPeliculaNoGusta(String pelicula) {
        String sql = "INSERT INTO Coleccion (Nombre, MeGusta, user_id, pelicula_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = con.prepareStatement(sql)) {
            statement.setString(1, pelicula);
            statement.setInt(2, 1);
            statement.setInt(3, id);

            int peliculaId = obtenerIdPelicula(pelicula);
            statement.setInt(4, peliculaId);

            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int obtenerIdPelicula(String pelicula) {
        String sql = "SELECT ID_old FROM Peliculas WHERE Nombre = ?";
        try (PreparedStatement statement = con.prepareStatement(sql)) {
            statement.setString(1, pelicula);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("ID_old");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new App(nombre, id).setVisible(true));
    }
}
