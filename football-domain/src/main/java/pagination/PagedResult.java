package pagination;

import java.util.List;

/**
 * Resultat pagine independant des frameworks de persistence/HTTP.
 */
public record PagedResult<T>(List<T> content,
                             int page,
                             int size,
                             long totalElements,
                             int totalPages,
                             boolean first,
                             boolean last,
                             String sortBy,
                             String direction) {
}

