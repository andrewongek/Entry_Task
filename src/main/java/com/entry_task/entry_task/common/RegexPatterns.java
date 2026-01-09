package com.entry_task.entry_task.common;

public class RegexPatterns {

  private RegexPatterns() {}

  // Common regex patterns used for validating API request parameters

  /**
   * ALPHANUMERIC_PATTERN: Validates simple identifiers or codes containing only ASCII letters and
   * digits.
   *
   * <p>Typical use cases: - reference codes - order numbers - short identifiers
   *
   * <p>Examples: - "ABC123" - "order99" - "X9"
   */
  public static final String ALPHANUMERIC_PATTERN = "^[a-zA-Z0-9]+$";

  /**
   * UUID_PATTERN: Validates standard UUID format (8-4-4-4-12 hexadecimal characters with hyphens).
   *
   * <p>Typical use cases: - resource identifiers - session tokens - unique keys
   *
   * <p>Examples: - "123e4567-e89b-12d3-a456-426614174000" - "f47ac10b-58cc-4372-a567-0e02b2c3d479"
   */
  public static final String UUID_PATTERN =
      "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$";

  /**
   * IDENTIFIER_PATTERN: Validates field or property names that map directly to entity attributes.
   * The value must start with a letter and may contain letters, digits, or underscores.
   *
   * <p>Typical use cases: - sort fields - filter fields - entity property names
   *
   * <p>Examples: - "id" - "createdAt" - "seller_id"
   */
  public static final String IDENTIFIER_PATTERN = "^[a-zA-Z][a-zA-Z0-9_]*$";

  /**
   * SORT_ORDER_PATTERN: Validates sort direction values. Only allows "ASC" or "DESC"
   * (case-insensitive).
   *
   * <p>Typical use cases: - pagination sorting - list ordering
   *
   * <p>Examples: - "ASC" - "DESC" - "asc"
   */
  public static final String SORT_ORDER_PATTERN = "^(?i)(ASC|DESC)$";

  /**
   * KEYWORD_PATTERN: Validates free-text search keywords while restricting potentially unsafe
   * characters. Allows Unicode letters, digits, spaces, and limited punctuation.
   *
   * <p>Typical use cases: - search keywords - name-based filtering
   *
   * <p>Examples: - "Pokemon" - "Pokémon Cards" - "Dragon Ball Z" - "Kid's Toys"
   */
  public static final String KEYWORD_PATTERN = "^[\\p{L}0-9 .,'-]+$";
}
