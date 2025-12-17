package com.entry_task.entry_task.exceptions;

import com.entry_task.entry_task.common.api.CustomApiResponse;
import jakarta.persistence.OptimisticLockException;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.kafka.common.errors.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private void logException(Exception ex, HttpServletRequest request, boolean includeStackTrace) {
        if (includeStackTrace) {
            log.error("Exception caught: {} | path={} | ", ex.getMessage(), request.getRequestURI(), ex);
        } else {
            log.warn("Exception caught: {} | path={}", ex.getMessage(), request.getRequestURI());
        }
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<CustomApiResponse<?>> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        logException(ex, request, false);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(CustomApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<CustomApiResponse<?>> handleIllegalState(IllegalStateException ex, HttpServletRequest request) {
        logException(ex, request, false);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(CustomApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(OptimisticLockException.class)
    public ResponseEntity<CustomApiResponse<Void>> handleOptimisticLock(OptimisticLockException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(CustomApiResponse.error("Concurrent updates detected. Please try again."));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<CustomApiResponse<?>> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpServletRequest request) {
        logException(ex, request, false);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(CustomApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<CustomApiResponse<?>> handleJsonParseError(HttpMessageNotReadableException ex, HttpServletRequest request) {
        logException(ex, request, false);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(CustomApiResponse.error("Malformed JSON in request"));
    }

    @ExceptionHandler(InvalidDataAccessResourceUsageException.class)
    public ResponseEntity<CustomApiResponse<?>> handleInvalidDataAccess(InvalidDataAccessResourceUsageException ex, HttpServletRequest request) {
        logException(ex, request, true);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(CustomApiResponse.error("Internal server error. Please try again later."));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<CustomApiResponse<?>> handleUsernameNotFound(UsernameNotFoundException ex, HttpServletRequest request) {
        logException(ex, request, false);
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(CustomApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(InvalidJwtException.class)
    public ResponseEntity<CustomApiResponse<?>> handleInvalidJwt(InvalidJwtException ex, HttpServletRequest request) {
        logException(ex, request, false);
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(CustomApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<CustomApiResponse<?>> handleInvalidRefreshToken(InvalidRefreshTokenException ex, HttpServletRequest request) {
        logException(ex, request, false);
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(CustomApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(FavouriteNotFoundException.class)
    public ResponseEntity<CustomApiResponse<?>> handleFavouriteNotFound(FavouriteNotFoundException ex, HttpServletRequest request) {
        logException(ex, request, false);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(CustomApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(ProductAlreadyFavouritedException.class)
    public ResponseEntity<CustomApiResponse<?>> handleProductAlreadyFavourited(ProductAlreadyFavouritedException ex, HttpServletRequest request) {
        logException(ex, request, false);
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(CustomApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<CustomApiResponse<?>> handleUserNotFound(UserNotFoundException ex, HttpServletRequest request) {
        logException(ex, request, false);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(CustomApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(ProductNotActiveException.class)
    public ResponseEntity<CustomApiResponse<?>> handleProductNotActive(ProductNotActiveException ex, HttpServletRequest request) {
        logException(ex, request, false);
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(CustomApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(InvalidCartItemException.class)
    public ResponseEntity<CustomApiResponse<?>> handleInvalidCartItem(InvalidCartItemException ex, HttpServletRequest request) {
        logException(ex, request, false);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(CustomApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<CustomApiResponse<?>> handleInsufficientStock(InsufficientStockException ex, HttpServletRequest request) {
        logException(ex, request, false);
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(CustomApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(CategoryAlreadyExistsException.class)
    public ResponseEntity<CustomApiResponse<?>> handleCategoryAlreadyExists(CategoryAlreadyExistsException ex, HttpServletRequest request) {
        logException(ex, request, false);
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(CustomApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<CustomApiResponse<?>> handleCategoryNotFound(CategoryNotFoundException ex, HttpServletRequest request) {
        logException(ex, request, false);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(CustomApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(CartNotFoundException.class)
    public ResponseEntity<CustomApiResponse<?>> handleCartNotFound(CartNotFoundException ex, HttpServletRequest request) {
        logException(ex, request, false);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(CustomApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(CartItemNotFoundException.class)
    public ResponseEntity<CustomApiResponse<?>> handleCartItemNotFound(CartItemNotFoundException ex, HttpServletRequest request) {
        logException(ex, request, false);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(CustomApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<CustomApiResponse<?>> handleProductNotFound(ProductNotFoundException ex, HttpServletRequest request) {
        logException(ex, request, false);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(CustomApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<CustomApiResponse<?>> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        logException(ex, request, false);
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(CustomApiResponse.error(ex.getMessage()));
    }

    // Wrong username / password
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<CustomApiResponse<?>> handleBadCredentials(BadCredentialsException ex, HttpServletRequest request) {
        logException(ex, request, false);
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(CustomApiResponse.error("Invalid username or password"));
    }

    // Any other authentication failure
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<CustomApiResponse<?>> handleAuthenticationException(AuthenticationException ex, HttpServletRequest request) {
        logException(ex, request, false);
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(CustomApiResponse.error("Authentication failed: " + ex.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<CustomApiResponse<?>> handleDataIntegrityViolation(DataIntegrityViolationException ex, HttpServletRequest request) {
        logException(ex, request, false);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(CustomApiResponse.error("Duplicate value violates unique constraint"));
    }

    // Optional – catch any unexpected runtime errors
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<CustomApiResponse<?>> handleRuntime(RuntimeException ex, HttpServletRequest request) {
        logException(ex, request, true);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(CustomApiResponse.error("Internal server error. Please try again later"));
    }
}
