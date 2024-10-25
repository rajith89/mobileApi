package com.udipoc.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentsResponse implements Serializable {
    private static final long serialVersionUID = 2452990684776944908L;
    /**
     * The doc type.
     */
    private String category;

    /**
     * The doc value.
     */
    private String value;
}
