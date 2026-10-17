package com.cuttingAPI.web;

import com.cuttingAPI.application.dto.CuttingRequest;
import com.cuttingAPI.application.service.CuttingService;
import com.cuttingAPI.domain.model.CuttingList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class CuttingController {

  private final CuttingService cuttingService;

  @Autowired
  public CuttingController(CuttingService cuttingService) {
    this.cuttingService = cuttingService;
  }

  // Fixed orientation of rectangular pieces (respects grain direction)
  @PostMapping("/cut_1")
  public CuttingList cutter1(@RequestBody CuttingRequest request) throws Exception {
    return cuttingService.executeCutOptimization(request, true);
  }

  // Free orientation of rectangular pieces (allows 90-degree rotation)
  @PostMapping("/cut_2")
  public CuttingList cutter2(@RequestBody CuttingRequest request) throws Exception {
    return cuttingService.executeCutOptimization(request, false);
  }
}
