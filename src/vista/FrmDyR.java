package vista;

import api.*;
import impl.JSONHandler;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class FrmDyR extends JDialog {
    private JPanel pnlPrincipal;
    private JPanel pnlTitulo;
    private JComboBox TipoBusqueda;
    private JTextField textFieldSocio;
    private JPanel pnlSearch;
    private JTabbedPane tabbedPaneDyR;
    private JPanel tabRecuperos;
    private JTabbedPane tabbedPaneR;
    private JPanel pnlNR;
    private JPanel pnlHR;
    private JPanel tabDesembolsos;
    private JTextField RSTextField;
    private JComboBox TipocomboBox;
    private JTextField TotalDeudaTxField;
    private JButton buscarButton;
    private JButton confirmBtn;
    private JTextField MontoTxField;
    private JTextField CUITTxField;
    private JScrollPane pnlSTable;
    private JScrollPane pnlSHTable;
    private JScrollPane pnlSDesembolsosTable;
    private JButton LDButton;
    private JTable deudasTable;
    private JTable recuperosTable;
    private JTable desembolsosTable;

    private Verificaciones verificar = new impl.Verificaciones();
    private FrmDyR self;

    private String filename = "./src/resources/socios.json";
    private API_JSONHandler file = new JSONHandler();
    private JSONObject jsonObject = (JSONObject) file.readJson(filename);

    public FrmDyR(Window owner, String Title) throws Exception {
        super(owner, Title);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        // Define el canvas según swing.
        this.setContentPane(this.pnlPrincipal);
        // Tamaño de la pantalla
        this.setSize(1000, 600);
        this.setResizable(false);
        // No permite volver a la pantalla anterior hasta cerrar esta
        this.setModal(true);
        // Establezco el comportamiento al cerrar la pantalla
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        // Inicia la pantalla centrada
        this.setLocationRelativeTo(null);

        this.asociareventos();
        this.self = this;



    }

    private void asociareventos(){
        confirmBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    crearRecupero();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        });
        buscarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    buscarSocio();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }

            }
        });
        TipocomboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (TipocomboBox.getSelectedItem().toString().equals("Parcial")){
                    MontoTxField.setEditable(true);
                    MontoTxField.setText("0.0");
                }else{
                    MontoTxField.setEditable(false);
                    MontoTxField.setText(TotalDeudaTxField.getText());
                }
            }
        });

        LDButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buscarDesembolsos();
            }
        });
    }

    private void buscarSocio() throws Exception {
        JSONArray socioList = (JSONArray) jsonObject.get("socios-participes");
        for (Object obj: socioList){
            JSONObject socio = (JSONObject) obj;
            String rs = socio.get("razon-social").toString();
            String cuit = socio.get("cuit").toString();
            String estado = socio.get("estado").toString();
            if (TipoBusqueda.getSelectedItem().toString().equals("CUIT")){
                if (verificar.CUITValido(textFieldSocio.getText())) {
                    if (cuit.equals(textFieldSocio.getText()) && estado.equals("Pleno")) {
                        CUITTxField.setText(cuit);
                        RSTextField.setText(rs);
                        if (pnlHR.isShowing()) {
                            crearTablaHistorialRecuperos((JSONArray) socio.get("recuperos"));
                        } else {
                            crearTablaDeudas((JSONArray) socio.get("deudas"));
                        }
                    }
                }
            }else if (TipoBusqueda.getSelectedItem().toString().equals("Razon Social")){
                if (rs.equals(textFieldSocio.getText()) && estado.equals("Pleno")){
                    CUITTxField.setText(cuit);
                    RSTextField.setText(rs);
                    if ( pnlHR.isShowing()){
                        crearTablaHistorialRecuperos((JSONArray) socio.get("recuperos"));
                    }else {
                        crearTablaDeudas((JSONArray) socio.get("deudas"));
                    }
                }
            }
        }
    }

    private void crearTablaDeudas(JSONArray deudas){
        String [] nombresColumnas = {"ID","Monto","Fecha Ven","Mora","Monto Mora","Subtotal"};
        DefaultTableModel modelo = new DefaultTableModel();
        for ( String column : nombresColumnas) {
            modelo.addColumn(column);
        }
        double total = 0;
        for ( Object deudaJ: deudas){
            ArrayList data = new ArrayList<>();
            Deuda deuda = new impl.Deuda((JSONObject) deudaJ);
            data.add(deuda.getIdDeuda());
            data.add(deuda.getMonto());
            data.add(deuda.getFechaDeuda());
            data.add(deuda.isAplicaMora());
            data.add(deuda.getMontoMora());
            data.add(calcularSubtotal(deuda));
            modelo.addRow(data.toArray());
            total += calcularSubtotal(deuda);
        }

        TotalDeudaTxField.setText(Double.toString(total));
        if (TipocomboBox.getSelectedItem().equals("Total")){
            MontoTxField.setText(Double.toString(total));
        }
        deudasTable = new JTable(modelo);

        pnlSTable.setViewportView(deudasTable);
    }

    private double calcularSubtotal( Deuda deuda){
        double subtotal = deuda.getMonto();
        if (deuda.isAplicaMora()) {
            if (deuda.getFechaDeuda().isBefore(LocalDate.now())) {
                subtotal = deuda.getMonto() + (deuda.getMontoMora() * ChronoUnit.DAYS.between(deuda.getFechaDeuda(),LocalDate.now()));
            }
        }
        return subtotal;
    }

    private void crearRecupero() throws Exception {
        ArrayList<String> idDeudas = new ArrayList<>();
        for (int i = 0; i < deudasTable.getRowCount(); i++) {
            idDeudas.add(deudasTable.getValueAt(i,0).toString());
        }
        Recupero nuevoR = new impl.Recupero(
                TipocomboBox.getSelectedItem().toString(),
                Double.parseDouble(MontoTxField.getText()),
                CUITTxField.getText(),
                idDeudas
        );

        JSONObject recupero = nuevoR.toJSON();
        cancelarDeudas();
        guardarDatos(recupero);

    }

    private void cancelarDeudas() throws Exception {
        String filename = "./src/resources/socios.json";
        API_JSONHandler file = new JSONHandler();
        JSONObject jsonObject = (JSONObject) file.readJson(filename);
        JSONArray socioList = (JSONArray) jsonObject.get("socios-participes");
        double monto = Double.parseDouble(MontoTxField.getText());
        for (Object s : socioList){
            JSONObject socio = (JSONObject) s;
            if (socio.get("cuit").equals(CUITTxField.getText())){
                JSONArray deudas = (JSONArray) socio.get("deudas");
                while (monto >= 0){
                    Deuda deuda = new impl.Deuda((JSONObject) deudas.get(0));
                    if (monto - calcularSubtotal(deuda) >= 0) {
                        monto -= calcularSubtotal(deuda);
                        System.out.println("Nuevo Monto - " + monto);
                        deudas.remove(0);
                    }
                    if ( monto == 0){
                        break;
                    }
                }
                break;
            }
        }
        jsonObject.put("socios-participes", socioList);
        file.writeJson(filename, jsonObject);
    }

    private void crearTablaHistorialRecuperos(JSONArray recuperos){
        String [] nombresColumnas = {"CUIT","Tipo", "Monto", "Deudas"};
        DefaultTableModel modelo = new DefaultTableModel();
        for ( String column : nombresColumnas) {
            modelo.addColumn(column);
        }
        for ( Object recupero: recuperos){
            ArrayList data = new ArrayList<>();
            Recupero recu = new impl.Recupero((JSONObject) recupero);
            data.add(recu.getIdSocioPleno());
            data.add(recu.getTipo());
            data.add(recu.getMonto());
            data.add(recu.getIdDeuda());
            modelo.addRow(data.toArray());
        }

        recuperosTable = new JTable(modelo);

        pnlSHTable.setViewportView(recuperosTable);

    }

    private void guardarDatos(JSONObject recupero) throws Exception {
        String filename = "./src/resources/socios.json";
        API_JSONHandler file = new JSONHandler();
        JSONObject jsonObject = (JSONObject) file.readJson(filename);
        JSONArray socioList = (JSONArray) jsonObject.get("socios-participes");
        for (Object s : socioList){
            JSONObject socio = (JSONObject) s;
            if (socio.get("cuit").equals(recupero.get("id-socio-pleno"))){
                JSONArray recuperos = (JSONArray) socio.get("recuperos");
                recuperos.add(recupero);
                socio.put("recuperos", recuperos);
                break;
            }
        }
        jsonObject.put("socios-participes", socioList);
        file.writeJson(filename, jsonObject);

    }

    private void buscarDesembolsos(){
        JSONArray desembolsos = (JSONArray) jsonObject.get("desembolsos");
        crearTablaDesembolsos(desembolsos);
    }

    private void crearTablaDesembolsos(JSONArray desembolsos){
        String [] nombresColumnas = {"id", "CUIT", "Razón social", "Monto", "Fecha Operado"};
        DefaultTableModel modelo = new DefaultTableModel();
        for ( String column : nombresColumnas) {
            modelo.addColumn(column);
        }
        for (Object des: desembolsos) {
            ArrayList data = new ArrayList<>();
            Desembolso desembolso = new impl.Desembolso((JSONObject) des);
            data.add(desembolso.getId());
            data.add(desembolso.getCuit());
            data.add(desembolso.getRazonSocial());
            data.add(desembolso.getMonto());
            data.add(desembolso.getFechaOp().toString());
            modelo.addRow(data.toArray());
        }
        desembolsosTable = new JTable(modelo);
        pnlSDesembolsosTable.setViewportView(desembolsosTable);

    }
}
