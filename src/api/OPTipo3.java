package api;

import org.json.simple.JSONObject;

import java.time.LocalDate;
import java.util.Date;

public interface OPTipo3 extends Operacion {
    String getCantCuotas();

    String getBanco();

    float getImporteTotal();

    float getTasa();

    LocalDate getFechaAcreditacion();

    String getSistema();

    int getCuotasImpagas();

    JSONObject toJSON();

    @Override
    String getCUITSolicitante();

    String getEstado();

    String getTipo();

    int getNumerooperacion();
}
