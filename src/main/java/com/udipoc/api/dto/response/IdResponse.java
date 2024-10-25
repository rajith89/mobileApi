package com.udipoc.api.dto.response;

import com.fasterxml.jackson.annotation.JsonFilter;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class IdResponse implements Serializable {
    private static final long serialVersionUID = 7156526077883281623L;

    /** The id. */
    private String id;

    /** The ver. */
    private String version;

    /** The timestamp. */
    private String responsetime;

    /** The err. */
    private List<ErrorDTO> errors;

    private Object metadata;

    /** The response. */
    @JsonFilter("responseFilter")
    private ResponseDTO response;
}
