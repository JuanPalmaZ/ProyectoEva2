package cl.paris.marketplace.ms.usuarios.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LegacySyncRequest(
       @JsonProperty("codigoAntiguo") String codigoAntiguo,
        @JsonProperty("tipoEntidad") String tipoEntidad,
        @JsonProperty("datosMigrados") String datosMigrados
) {}