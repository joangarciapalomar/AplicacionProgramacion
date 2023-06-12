package org.example;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import static java.awt.Color.darkGray;

public class InicioDeSesion extends JFrame {
    static Connection con = DatabaseConnection.getInstance().getConnection();

    JPanel panel1 = new JPanel();
    JPanel panel2 = new JPanel();
    JPanel panel3 = new JPanel();
    JLabel usuario = new JLabel("Usuario:");
    JLabel contrasenya = new JLabel("Contraseña:");
    JTextField usuarioT = new JTextField();
    JPasswordField contra = new JPasswordField();
    JButton inicio = new JButton("Iniciar Sesión");
    JButton crearUser = new JButton("Crear Usuario");

    public InicioDeSesion() {
        createGUI();
        attachEvents();
    }

    private void createGUI() {
        setTitle("Inicio de Sesión");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Container container1 = getContentPane();
        container1.setBackground(darkGray);
        container1.setLayout(new BoxLayout(container1, BoxLayout.Y_AXIS));
        container1.setPreferredSize(new Dimension(280, 112));
        container1.add(panel1);
        panel1.setBackground(darkGray);
        container1.add(panel2);
        panel2.setBackground(darkGray);
        container1.add(panel3);
        panel3.setBackground(darkGray);

        panel1.add(usuario);
        usuario.setForeground(Color.WHITE);
        panel1.add(usuarioT);
        usuarioT.setBorder(BorderFactory.createLineBorder(darkGray, 1));
        usuarioT.setPreferredSize(new Dimension(100, 20));
        panel1.setBorder(new EmptyBorder(10, 28, 0, 0));

        panel2.add(contrasenya);
        contrasenya.setForeground(Color.WHITE);
        panel2.add(contra);
        contra.setBorder(BorderFactory.createLineBorder(darkGray, 1));
        contra.setPreferredSize(new Dimension(100, 20));

        panel3.add(crearUser);
        crearUser.setBackground(Color.lightGray);
        crearUser.setForeground(darkGray);
        crearUser.setRolloverEnabled(false);
        panel3.add(inicio);
        inicio.setBackground(Color.gray);
        inicio.setForeground(Color.WHITE);
        inicio.setRolloverEnabled(false);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void attachEvents() {


        inicio.addActionListener(e -> {
            String usuario = usuarioT.getText();
            char[] contrasenya = contra.getPassword();

            if (validarUsuarios(usuario, contrasenya)) {

                App app = new App(usuario, getId(usuario));
                app.setVisible(true);
                dispose();

                JOptionPane.showMessageDialog(InicioDeSesion.this, "Bienvenido");
            } else {

                JOptionPane.showMessageDialog(InicioDeSesion.this, "Usuario o contraseña invalidos", "Error de inicio de sesión", JOptionPane.ERROR_MESSAGE);
            }
        });
        crearUser.addActionListener(e -> {
            String nombreUsuario = usuarioT.getText();
            char[] contrasenya = contra.getPassword();

            if (crearUsuario(nombreUsuario, contrasenya)) {
                JOptionPane.showMessageDialog(InicioDeSesion.this, "Usuario creado exitosamente");
            } else {
                JOptionPane.showMessageDialog(InicioDeSesion.this, "Error al crear el usuario", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

    }

    private boolean crearUsuario(String nombre, char[] contrasenya) {
        if (existeUsuario(nombre)) {
            JOptionPane.showMessageDialog(InicioDeSesion.this, "Ya existe un usuario con ese nombre", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String sql = "INSERT INTO Usuarios (Nombre, Contraseña) VALUES (?, ?)";
        try (PreparedStatement statement = con.prepareStatement(sql)) {
            statement.setString(1, nombre);
            statement.setString(2, new String(contrasenya));
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    private boolean existeUsuario(String nombre) {
        String sql = "SELECT COUNT(*) FROM Usuarios WHERE Nombre = ?";
        try (PreparedStatement statement = con.prepareStatement(sql)) {
            statement.setString(1, nombre);
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


    private int getId(String nombre) {
        String sql = "SELECT ID FROM Usuarios WHERE Nombre = ?";
        int id = -1;

        try (PreparedStatement statement = con.prepareStatement(sql)) {
            statement.setString(1, nombre);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                id = resultSet.getInt("ID");
            }

            resultSet.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return id;
    }
    private boolean validarUsuarios(String usuario, char[] contrasenya) {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE Nombre = ? AND Contraseña = ?";
        try (PreparedStatement statement = con.prepareStatement(sql)) {
            statement.setString(1, usuario);
            statement.setString(2, new String(contrasenya));
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new InicioDeSesion().setVisible(true));
    }
}
