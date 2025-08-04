package vistas;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.SwingConstants;
import java.awt.FlowLayout;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ActionEvent;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.JTextArea;

public class VistaEstacion extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JScrollPane scrollPane;
	private JScrollPane scrollPane_1;
	private JTextArea textAreaMensajesControl;
	private JTextArea textAreaMensajesDifusion;

	/**
	 * Create the frame.
	 */
	public VistaEstacion(String nombreEstacion) {

		setTitle("Aplicación de estación '" + nombreEstacion + "'");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 768, 462);
		contentPane = new JPanel();
		contentPane.setBackground(UIManager.getColor("Button.darkShadow"));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JPanel panel = new JPanel();
		panel.setBackground(UIManager.getColor("Button.darkShadow"));
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		contentPane.add(panel, BorderLayout.NORTH);

		JLabel lblAplicacinDeLa = new JLabel("Aplicación de estación '" + nombreEstacion + "'");
		lblAplicacinDeLa.setVerticalAlignment(SwingConstants.TOP);
		lblAplicacinDeLa.setHorizontalAlignment(SwingConstants.CENTER);
		lblAplicacinDeLa.setFont(new Font("Dialog", Font.BOLD | Font.ITALIC, 18));
		panel.add(lblAplicacinDeLa);

		JPanel panel_1 = new JPanel();
		panel_1.setBackground(UIManager.getColor("Button.darkShadow"));
		contentPane.add(panel_1, BorderLayout.SOUTH);

		JButton btnNewButton = new JButton("Salir");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		panel_1.add(btnNewButton);

		JPanel panel_2 = new JPanel();
		panel_2.setBackground(UIManager.getColor("Button.darkShadow"));
		contentPane.add(panel_2, BorderLayout.CENTER);
		GridBagLayout gbl_panel_2 = new GridBagLayout();
		gbl_panel_2.columnWidths = new int[] { 20, 0, 20, 0, 20, 0 };
		gbl_panel_2.rowHeights = new int[] { 20, 0, 0, 10, 0 };
		gbl_panel_2.columnWeights = new double[] { 0.0, 1.0, 0.0, 1.0, 0.0, Double.MIN_VALUE };
		gbl_panel_2.rowWeights = new double[] { 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE };
		panel_2.setLayout(gbl_panel_2);

		JLabel lblNewLabel_1 = new JLabel("Mensajes de Difusion Enviados:");
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_1.gridx = 1;
		gbc_lblNewLabel_1.gridy = 1;
		panel_2.add(lblNewLabel_1, gbc_lblNewLabel_1);

		JLabel lblNewLabel_2 = new JLabel("Terminal de control:");
		GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
		gbc_lblNewLabel_2.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel_2.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_2.gridx = 3;
		gbc_lblNewLabel_2.gridy = 1;
		panel_2.add(lblNewLabel_2, gbc_lblNewLabel_2);

		scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPane.gridx = 1;
		gbc_scrollPane.gridy = 2;
		panel_2.add(scrollPane, gbc_scrollPane);

		textAreaMensajesDifusion = new JTextArea();
		textAreaMensajesDifusion.setEditable(false);
		textAreaMensajesDifusion.setBackground(UIManager.getColor("Button.disabledToolBarBorderBackground"));
		textAreaMensajesDifusion.setLineWrap(true);
		scrollPane.setViewportView(textAreaMensajesDifusion);

		scrollPane_1 = new JScrollPane();
		scrollPane_1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		GridBagConstraints gbc_scrollPane_1 = new GridBagConstraints();
		gbc_scrollPane_1.fill = GridBagConstraints.BOTH;
		gbc_scrollPane_1.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPane_1.gridx = 3;
		gbc_scrollPane_1.gridy = 2;
		panel_2.add(scrollPane_1, gbc_scrollPane_1);

		textAreaMensajesControl = new JTextArea();
		textAreaMensajesControl.setEditable(false);
		textAreaMensajesControl.setBackground(UIManager.getColor("Button.disabledToolBarBorderBackground"));
		textAreaMensajesControl.setLineWrap(true);
		scrollPane_1.setViewportView(textAreaMensajesControl);
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

	// Para lanzar la vista
	public void lanzar() {
		this.setVisible(true);
	}
}
