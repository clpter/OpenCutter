package com.cuttingAPI.application.service;

import com.cuttingAPI.application.dto.CuttingRequest;
import com.cuttingAPI.domain.model.CuttingList;
import com.cuttingAPI.domain.service.CuttingOptimizer;
import com.cuttingAPI.domain.strategy.packing.MultiPassGuillotineStrategy;
import com.cuttingAPI.web.CustomException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * Application service orchestrating 2D cutting optimization use cases.
 */
@Service
public class CuttingService {

  public CuttingList executeCutOptimization(CuttingRequest request, boolean fixedDirection) throws Exception {
    if (request == null) {
      throw new CustomException("Invalid request: CuttingRequest cannot be null");
    }

    int pieceCount = request.getPieces() != null ? request.getPieces().size() : 0;
    if (pieceCount > 500) {
      throw new CustomException("Calculation limit exceeded: Maximum allowed candidate pieces is 500 (received "
          + pieceCount + "). Please reduce piece count or group smaller items.");
    }

    CuttingOptimizer optimizer = new CuttingOptimizer(
        request.getPieces() != null ? request.getPieces() : new ArrayList<>(),
        request.getStock() != null ? request.getStock() : new ArrayList<>(),
        new MultiPassGuillotineStrategy(30, 50, request.getPreferVerticalCuts(), request.getMaxCuttingLength()));

    if (fixedDirection) {
      optimizer.cutFixedDirection();
    } else {
      optimizer.cutWithRotation();
    }

    return optimizer.getCuttingList();
  }
}
