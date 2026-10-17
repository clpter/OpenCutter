package com.cuttingAPI.domain.strategy.packing;

import com.cuttingAPI.domain.model.CuttingList;
import com.cuttingAPI.domain.model.Rectangle;

import java.util.List;

/**
 * Packs candidate pieces into stock sheets using 2D Guillotine cutting rules.
 *
 * @param pieces        candidate pieces to pack
 * @param stock         available stock sheets
 * @param allowRotation flag allowing 90-degree rotation
 * @return populated CuttingList domain aggregate containing placed positions
 *         and waste off-cuts
 */
public interface PackingStrategy {
  CuttingList pack(List<Rectangle> pieces, List<Rectangle> stock, boolean allowRotation);
}
