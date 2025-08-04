package vistas;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import cliente.ClientController;

import java.awt.Color;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.SwingConstants;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class VistaCliente extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	private JTextField textFieldComandos;
	private JTextField textFieldServidoresRegistrados;
	private JTextArea textAreaMensajesDifusion;
	private JTextArea textAreaMensajesControl;
	private JScrollPane scrollPane;
	private JScrollPane scrollPane_1;
	private ClientController clientController; // Controlador de cliente que usa la vista para poder llamar al funcion
												// processComand cuando se introduzca uno

	/**
	 * Create the frame.
	 */
	public VistaCliente() {

		setTitle("Aplicación de Cliente");
		setBackground(Color.GRAY);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1200, 700);
		contentPane = new JPanel();
		contentPane.setBackground(Color.GRAY);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		panel.setBackground(Color.GRAY);
		contentPane.add(panel, BorderLayout.SOUTH);

		JButton btnNewButton = new JButton("Salir");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		panel.add(btnNewButton);

		JPanel panel_1 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_1.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		panel_1.setBackground(Color.GRAY);
		contentPane.add(panel_1, BorderLayout.NORTH);

		JLabel lblNewLabel = new JLabel("Aplicación de Cliente");
		lblNewLabel.setVerticalAlignment(SwingConstants.TOP);
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setFont(new Font("Dialog", Font.BOLD | Font.ITALIC, 18));
		panel_1.add(lblNewLabel);

		JPanel panel_2 = new JPanel();
		panel_2.setBackground(Color.GRAY);
		contentPane.add(panel_2, BorderLayout.CENTER);
		GridBagLayout gbl_panel_2 = new GridBagLayout();
		gbl_panel_2.columnWidths = new int[] { 20, 0, 20, 0, 20, 0 };
		gbl_panel_2.rowHeights = new int[] { 20, 0, 0, 10, 0, 0, 10, 0, 0, 0, 10, 0 };
		gbl_panel_2.columnWeights = new double[] { 0.0, 1.0, 0.0, 1.0, 0.0, Double.MIN_VALUE };
		gbl_panel_2.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				Double.MIN_VALUE };
		panel_2.setLayout(gbl_panel_2);

		JLabel lblNewLabel_4 = new JLabel("Servidores Resgistrados:");
		GridBagConstraints gbc_lblNewLabel_4 = new GridBagConstraints();
		gbc_lblNewLabel_4.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel_4.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_4.gridx = 1;
		gbc_lblNewLabel_4.gridy = 1;
		panel_2.add(lblNewLabel_4, gbc_lblNewLabel_4);

		textFieldServidoresRegistrados = new JTextField();
		textFieldServidoresRegistrados.setEditable(false);
		textFieldServidoresRegistrados.setBackground(Color.LIGHT_GRAY);
		GridBagConstraints gbc_textFieldServidoresRegistrados = new GridBagConstraints();
		gbc_textFieldServidoresRegistrados.gridwidth = 3;
		gbc_textFieldServidoresRegistrados.insets = new Insets(0, 0, 5, 5);
		gbc_textFieldServidoresRegistrados.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldServidoresRegistrados.gridx = 1;
		gbc_textFieldServidoresRegistrados.gridy = 2;
		panel_2.add(textFieldServidoresRegistrados, gbc_textFieldServidoresRegistrados);
		textFieldServidoresRegistrados.setColumns(10);

		JLabel lblNewLabel_1 = new JLabel("Mensajes de Difusion Recibidos:");
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_1.gridx = 1;
		gbc_lblNewLabel_1.gridy = 4;
		panel_2.add(lblNewLabel_1, gbc_lblNewLabel_1);

		JLabel lblNewLabel_2 = new JLabel("Terminal de control:");
		GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
		gbc_lblNewLabel_2.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel_2.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_2.gridx = 3;
		gbc_lblNewLabel_2.gridy = 4;
		panel_2.add(lblNewLabel_2, gbc_lblNewLabel_2);

		scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.gridheight = 5;
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPane.gridx = 1;
		gbc_scrollPane.gridy = 5;
		panel_2.add(scrollPane, gbc_scrollPane);

		textAreaMensajesDifusion = new JTextArea();
		textAreaMensajesDifusion.setEditable(false);
		textAreaMensajesDifusion.setBackground(Color.LIGHT_GRAY);
		textAreaMensajesDifusion.setLineWrap(true);
		scrollPane.setViewportView(textAreaMensajesDifusion);

		scrollPane_1 = new JScrollPane();
		scrollPane_1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		GridBagConstraints gbc_scrollPane_1 = new GridBagConstraints();
		gbc_scrollPane_1.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPane_1.fill = GridBagConstraints.BOTH;
		gbc_scrollPane_1.gridx = 3;
		gbc_scrollPane_1.gridy = 5;
		panel_2.add(scrollPane_1, gbc_scrollPane_1);

		textAreaMensajesControl = new JTextArea();
		textAreaMensajesControl.setEditable(false);
		textAreaMensajesControl.setBackground(Color.LIGHT_GRAY);
		textAreaMensajesControl.setLineWrap(true);
		scrollPane_1.setViewportView(textAreaMensajesControl);

		JLabel lblNewLabel_3 = new JLabel("Enviar comando:");
		GridBagConstraints gbc_lblNewLabel_3 = new GridBagConstraints();
		gbc_lblNewLabel_3.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel_3.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_3.gridx = 3;
		gbc_lblNewLabel_3.gridy = 7;
		panel_2.add(lblNewLabel_3, gbc_lblNewLabel_3);

		textFieldComandos = new JTextField();
		textFieldComandos.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getExtendedKeyCode() == KeyEvent.VK_ENTER) {

					String str = textFieldComandos.getText();
					textFieldComandos.setText("");
					clientController.processComand(str);
				}
			}
		});
		textFieldComandos.setBackground(Color.LIGHT_GRAY);
		GridBagConstraints gbc_textFieldComandos = new GridBagConstraints();
		gbc_textFieldComandos.insets = new Insets(0, 0, 5, 5);
		gbc_textFieldComandos.fill = GridBagConstraints.HORIZONTAL;
		gbc_textFieldComandos.gridx = 3;
		gbc_textFieldComandos.gridy = 8;
		panel_2.add(textFieldComandos, gbc_textFieldComandos);
		textFieldComandos.setColumns(10);

		JPanel panel_3 = new JPanel();
		panel_3.setBackground(Color.GRAY);
		GridBagConstraints gbc_panel_3 = new GridBagConstraints();
		gbc_panel_3.anchor = GridBagConstraints.SOUTH;
		gbc_panel_3.insets = new Insets(0, 0, 5, 5);
		gbc_panel_3.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel_3.gridx = 3;
		gbc_panel_3.gridy = 9;
		panel_2.add(panel_3, gbc_panel_3);

		JButton btnNewButton_1 = new JButton("Enviar");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String str = textFieldComandos.getText();
				textFieldComandos.setText("");
				clientController.processComand(str);
			}
		});
		panel_3.add(btnNewButton_1);
	}

	// Metodo para añadir informacion al panel de difusion
	public synchronized void writeInDifusion(String content) {
		textAreaMensajesDifusion.append(content + "\n");
		scrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				e.getAdjustable().setValue(e.getAdjustable().getMaximum());
			}
		});
	}

	// Metodo para añadir informacion al panel de control
	public synchronized void writeInControl(String content) {
		textAreaMensajesControl.append("prompt> " + content + "\n");
		scrollPane_1.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
			public void adjustmentValueChanged(AdjustmentEvent e) {
				e.getAdjustable().setValue(e.getAdjustable().getMaximum());
			}
		});
	}

	// Metodo para escribir en el textfield de estaciones registradas
	public void writeInEstaciones(String content) {
		textFieldServidoresRegistrados.setText(content);
	}

	// Metodo para vaciar el panel de control
	public void clearControlWindow() {
		textAreaMensajesControl.setText("");
	}

	// Metodo para vaciar el panel de difusion
	public void clearDifusionWindow() {
		textAreaMensajesDifusion.setText("");
	}

	// Para lanzar la vista
	public void lanzar() {
		this.setVisible(true);
	}

	// Para recibir un controlador de cliente
	public void setClientController(ClientController clientController) {
		this.clientController = clientController;
	}
}
