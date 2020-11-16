package vista;

import api.API_JSONHandler;
import api.Accionista;
import api.Deuda;
import api.Verificaciones;
import impl.JSONHandler;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;

import static javax.swing.JOptionPane.showMessageDialog;


public class FrmSocios extends JDialog{
    private JPanel pnlPrincipal;
    private JPanel pnlTitulo;
    private JTabbedPane pnlTabPanel;
    private JPanel pnlDocumentacion;
    private JTextField ingreseElCUITConTextField;
    private JTabbedPane tabbedPane1;
    private JButton buscarAccionistaButton;
    private JButton eliminarAccionistaButton;
    private JTextField textFieldCuit;
    private JTextField textFieldRazon;
    private JScrollPane PnlsAccionistas;
    private JPanel pnlAccionistas;
    private JButton crearAccionistaButton;
    private JSpinner spinnerParticipacion;
    private JPanel pnluntitled;
    private JButton cambiarColorButton;
    private JTable accionistasTabla;

    private Verificaciones verif = new impl.Verificaciones();

    private String filename = "./src/resources/socios.json";
    private API_JSONHandler file = new JSONHandler();
    private JSONObject jsonObject = (JSONObject) file.readJson(filename);
    private JSONArray accionistas;
    private DefaultTableModel modelos = new DefaultTableModel();
    private JSONObject socioborrar;
    private JSONArray socioList;

    public FrmSocios(Window owner, String Title) throws Exception {
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
        this.setSize(1000,600);
        // No permite volver a la pantalla anterior hasta cerrar esta
        this.setModal(true);
        // Establezco el comportamiento al cerrar la pantalla
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        // Inicia la pantalla centrada
        this.setLocationRelativeTo(null);
        this.pnlTabPanel.setBackgroundAt(0,Color.red);
        this.pnlTabPanel.setBackgroundAt(1,Color.blue);

        this.events();
        buscarAccionistaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean datosCorrectosFlag = true;

                //Toma Razon Social y checkea si esta vacio
                String textRazon="";
                textRazon= textFieldRazon.getText();
                if (textRazon.isEmpty()){
                    showMessageDialog(null, "El campo Razon Social es mandatorio, por favor ingrese el dato solicitado");
                    datosCorrectosFlag = false;
                }

                //Checkea el CUIT
                String textCuit="";
                textCuit=textFieldCuit.getText();
                if (verif.CUITValido(textCuit)){
                }
                else {
                    showMessageDialog(null, "El CUIT ingresado es invalido");
                    datosCorrectosFlag = false;
                }

                //Toma el Porcentaje de Participacion desde el Spinner
               /* Object participacion;
                participacion = spinnerParticipacion.getValue();
                int partint;
                partint= (Integer) participacion;
                if(partint <= 0){
                    showMessageDialog(null, "El porcentaje de Participacion debe ser menor o igual a 0");
                    datosCorrectosFlag = false;
                }
                if(partint >=100){
                    showMessageDialog(null,"El porcentaje de Participacion debe ser superior al 99%");
                    datosCorrectosFlag = false;
                }
                if (datosCorrectosFlag==true){

                }*/
                crearTabla(accionistas);
            }
        });
        /*crearAccionistaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean datosCorrectosFlag = true;

                //Toma Razon Social y checkea si esta vacio
                String textRazon="";
                textRazon= textFieldRazon.getText();
                if (textRazon.isEmpty()){
                    showMessageDialog(null, "El campo Razon Social es mandatorio, por favor ingrese el dato solicitado");
                    datosCorrectosFlag = false;
                }

                //Checkea el CUIT
                String textCuit="";
                textCuit=textFieldCuit.getText();
                if (verif.CUITValido(textCuit)){
                }
                else {
                    showMessageDialog(null, "El CUIT ingresado es invalido");
                    datosCorrectosFlag = false;
                }

                //Toma el Porcentaje de Participacion desde el Spinner
                Object participacion;
                participacion = spinnerParticipacion.getValue();
                int partint;
                partint= (Integer) participacion;
                if(partint <= 0){
                    showMessageDialog(null, "El porcentaje de Participacion debe ser menor o igual a 0");
                    datosCorrectosFlag = false;
                }
                if(partint >=100){
                    showMessageDialog(null,"El porcentaje de Participacion debe ser superior al 99%");
                    datosCorrectosFlag = false;
                }
                if (datosCorrectosFlag==true){

                }
            }
        });*/
        eliminarAccionistaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean datosCorrectosFlag = true;

                //Toma Razon Social y checkea si esta vacio
                String textRazon="";
                textRazon= textFieldRazon.getText();
                if (textRazon.isEmpty()){
                    showMessageDialog(null, "El campo Razon Social es mandatorio, por favor ingrese el dato solicitado");
                    datosCorrectosFlag = false;
                }

                //Checkea el CUIT
                String textCuit="";
                textCuit=textFieldCuit.getText();
                if (verif.CUITValido(textCuit)){
                }
                else {
                    showMessageDialog(null, "El CUIT ingresado es invalido");
                    datosCorrectosFlag = false;
                }
                modelos= (DefaultTableModel) accionistasTabla.getModel();

                if(accionistasTabla.getSelectedRow() != -1) {
                    // Elimina la fila seleccionada
                    String cuit= accionistasTabla.getValueAt(accionistasTabla.getSelectedRow(),0).toString();
                    try {
                        eliminarAccionista(cuit);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                    modelos.removeRow(accionistasTabla.getSelectedRow());
                    JOptionPane.showMessageDialog(null, "Se elimino la fila correctamente");
                    PnlsAccionistas.setViewportView(accionistasTabla);
                }
            }
        });
        textFieldRazon.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
            }

            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                try {
                    buscarAccionista();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        });
    }

    private void crearTabla(JSONArray accionistas) {
        String[] nombresColumnas = {"Cuit Accionista", "Razon Social", "Porcentaje Participacion"};
        DefaultTableModel modelo = new DefaultTableModel();
        for (String column : nombresColumnas) {
            modelo.addColumn(column);
        }
        for (Object accionistasJ : accionistas) {
            ArrayList data = new ArrayList<>();
            Accionista accionista = new impl.Accionista((JSONObject) accionistasJ);
            data.add(accionista.getCUITAccionista());
            data.add(accionista.getRazonsocial());
            data.add(accionista.getPorcParticipacion());
            modelo.addRow(data.toArray());
        }
        accionistasTabla= new JTable(modelo);
        PnlsAccionistas.setViewportView(accionistasTabla);
    }

    private void buscarAccionista() throws Exception {
        socioList = (JSONArray) jsonObject.get("socios-participes");
        for (Object obj: socioList){
            socioborrar = (JSONObject) obj;
            String rs = socioborrar.get("razon-social").toString();
            String cuit = socioborrar.get("cuit").toString();
                if (cuit.equals(textFieldCuit.getText())){
                    textFieldRazon.setText(rs);
                    accionistas= (JSONArray) socioborrar.get("accionistas");
                }

        }
    }
    private void eliminarAccionista(String cuit) throws Exception {
        for (Object ac:accionistas){
            JSONObject accionis= (JSONObject) ac;
            if(accionis.get("cuit-accionista").equals(cuit)){
                accionistas.remove(accionistas.indexOf(ac));
                break;
            }
        }
        socioborrar.put("accionistas",accionistas);
        jsonObject.put("socios-participes",socioList);
        file.writeJson(filename,jsonObject);
    }
    private void events(){

    }

}

