package impl;

import org.json.simple.JSONObject;

public class Accionista implements api.Accionista {

    private String CUITAccionista;
    private String razonsocial;
    private Double porcParticipacion;

    public Accionista(JSONObject accionistasJ) {
        this.CUITAccionista= (String) accionistasJ.get("cuit-accionista");
        this.porcParticipacion= Double.parseDouble(accionistasJ.get("porcentaje-participacion").toString());
        this.razonsocial= (String) accionistasJ.get("razon-social-ac");

    }
    public Accionista(String cuitac,String razon, Double por){
        this.CUITAccionista=  cuitac;
        this.porcParticipacion= por;
        this.razonsocial= razon;
    }


    /*======GETTERS=======*/
    @Override
    public String getCUITAccionista() {
        return CUITAccionista;
    }

    @Override
    public String getRazonsocial() {
        return razonsocial;
    }

    @Override
    public Double getPorcParticipacion() {
        return porcParticipacion;
    }

    public JSONObject toJSON(){
        JSONObject accionista = new JSONObject();
        accionista.put("cuit-accionista", this.CUITAccionista);
        accionista.put("razon-social-ac", this.razonsocial);
        accionista.put("porcentaje-participacion", this.porcParticipacion);
        return accionista;
    }
}
