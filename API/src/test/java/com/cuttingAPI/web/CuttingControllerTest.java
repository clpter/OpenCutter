package com.cuttingAPI.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("CuttingController Presentation / REST Layer Integration & Web Endpoint Tests")
class CuttingControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private CuttingController controller;

  @Autowired
  private GlobalExceptionHandler globalHandler;

  @Nested
  @DisplayName("Endpoint /api/cut_1 Tests (Fixed Orientation)")
  class Cut1EndpointTests {

    @Test
    @DisplayName("Should successfully calculate cut_1 with English JSON payload and return HTTP 200 OK")
    void shouldReturnHttp200_WhenValidCut1Request() throws Exception {
      String requestJson = """
          {
              "stock": [
                  {"x": 0, "y": 0, "width": 100, "height": 100, "id": 1}
              ],
              "pieces": [
                  {"x": 0, "y": 0, "width": 40, "height": 40, "id": 10, "assignment": 0, "number": 1},
                  {"x": 0, "y": 0, "width": 30, "height": 30, "id": 20, "assignment": 0, "number": 2}
              ]
          }
          """;

      mockMvc.perform(post("/api/cut_1")
          .contentType(MediaType.APPLICATION_JSON)
          .content(requestJson))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.placedPieces").isArray())
          .andExpect(jsonPath("$.stock").isArray())
          .andExpect(jsonPath("$.waste").isArray());
    }

    @Test
    @DisplayName("Should maintain backward compatibility with legacy German aliases")
    void shouldSupportLegacyGermanAliases_WhenCut1Request() throws Exception {
      String requestJson = """
          {
              "bestand": [
                  {"x": 0, "y": 0, "width": 100, "height": 100, "id": 1}
              ],
              "stücke": [
                  {"x": 0, "y": 0, "width": 40, "height": 40, "id": 10, "zuordnung": 0, "number": 1}
              ]
          }
          """;

      mockMvc.perform(post("/api/cut_1")
          .contentType(MediaType.APPLICATION_JSON)
          .content(requestJson))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.placedPieces").isArray());
    }

    @Test
    @DisplayName("Should handle empty pieces list in /api/cut_1 gracefully")
    void shouldHandleEmptyPieces_WhenCut1Request() throws Exception {
      String requestJson = """
          {
              "stock": [
                  {"x": 0, "y": 0, "width": 100, "height": 100, "id": 1}
              ],
              "pieces": []
          }
          """;

      mockMvc.perform(post("/api/cut_1")
          .contentType(MediaType.APPLICATION_JSON)
          .content(requestJson))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.placedPieces").isEmpty());
    }
  }

  @Nested
  @DisplayName("Endpoint /api/cut_2 Tests (Free Rotation)")
  class Cut2EndpointTests {

    @Test
    @DisplayName("Should successfully calculate cut_2 with piece rotation and return HTTP 200 OK")
    void shouldReturnHttp200_WhenValidCut2RequestWithRotation() throws Exception {
      String requestJson = """
          {
              "stock": [
                  {"x": 0, "y": 0, "width": 50, "height": 100, "id": 1}
              ],
              "pieces": [
                  {"x": 0, "y": 0, "width": 80, "height": 40, "id": 10, "assignment": 0, "number": 1}
              ]
          }
          """;

      mockMvc.perform(post("/api/cut_2")
          .contentType(MediaType.APPLICATION_JSON)
          .content(requestJson))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.placedPieces").isArray())
          .andExpect(jsonPath("$.stock").isArray());
    }

    @Test
    @DisplayName("Should handle multiple stock sheets in /api/cut_2")
    void shouldHandleMultipleStockSheets_WhenCut2Request() throws Exception {
      String requestJson = """
          {
              "stock": [
                  {"x": 0, "y": 0, "width": 100, "height": 100, "id": 1},
                  {"x": 0, "y": 0, "width": 200, "height": 200, "id": 2}
              ],
              "pieces": [
                  {"x": 0, "y": 0, "width": 60, "height": 60, "id": 10, "assignment": 0, "number": 1}
              ]
          }
          """;

      mockMvc.perform(post("/api/cut_2")
          .contentType(MediaType.APPLICATION_JSON)
          .content(requestJson))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.placedPieces").isArray());
    }
  }

  @Nested
  @DisplayName("CORS and Exception Handling Tests")
  class ExceptionAndCorsTests {

    @Test
    @DisplayName("handleCustomException should return HTTP 400 Bad Request with custom message")
    void shouldReturnBadRequest_WhenCustomExceptionThrown() {
      String errorMsg = "Calculation was too demanding; try grouping smaller pieces together";
      CustomException ex = new CustomException(errorMsg);

      ResponseEntity<String> response = globalHandler.handleCustomException(ex);

      assertNotNull(response);
      assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
      assertEquals(errorMsg, response.getBody());
    }

    @Test
    @DisplayName("handleGenericException should return HTTP 500 Internal Server Error with generic message")
    void shouldReturnInternalServerError_WhenGenericExceptionThrown() {
      Exception ex = new RuntimeException("Unexpected runtime failure");

      ResponseEntity<String> response = globalHandler.handleGenericException(ex);

      assertNotNull(response);
      assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
      assertEquals("An unexpected error occurred", response.getBody());
    }

    @Test
    @DisplayName("Should return HTTP 400 Bad Request when piece count exceeds 500 limit")
    void shouldReturnBadRequest_WhenPieceCountExceeds500() throws Exception {
      StringBuilder piecesJson = new StringBuilder("[");
      for (int i = 0; i < 501; i++) {
        if (i > 0)
          piecesJson.append(",");
        piecesJson.append(String.format(
            "{\"x\":0,\"y\":0,\"width\":100,\"height\":100,\"id\":%d,\"assignment\":0,\"number\":%d}", i, i + 1));
      }
      piecesJson.append("]");

      String requestJson = String.format("""
          {
              "stock": [{"x":0,"y":0,"width":2800,"height":2070,"id":1}],
              "pieces": %s
          }
          """, piecesJson.toString());

      mockMvc.perform(post("/api/cut_1")
          .contentType(MediaType.APPLICATION_JSON)
          .content(requestJson))
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should support CORS preflight options request on /api/cut_1")
    void shouldSupportCorsPreflight() throws Exception {
      mockMvc.perform(options("/api/cut_1")
          .header("Origin", "http://localhost:3000")
          .header("Access-Control-Request-Method", "POST"))
          .andExpect(status().isOk())
          .andExpect(header().exists("Access-Control-Allow-Origin"));
    }
  }

  @Nested
  @DisplayName("Large Dataset API Simulation Tests (With and Without Rotation)")
  class LargeDatasetApiSimulationTests {

    private final String datasetJson = """
        {
            "pieces": [
                {"height": 270, "id": 0, "number": 1, "width": 890},
                {"height": 270, "id": 1, "number": 2, "width": 890},
                {"height": 270, "id": 2, "number": 3, "width": 890},
                {"height": 270, "id": 3, "number": 4, "width": 890},
                {"height": 495, "id": 4, "number": 5, "width": 890},
                {"height": 495, "id": 5, "number": 6, "width": 890},
                {"height": 495, "id": 6, "number": 6, "width": 890},
                {"height": 594, "id": 7, "number": 7, "width": 880},
                {"height": 595, "id": 8, "number": 8, "width": 161},
                {"height": 595, "id": 9, "number": 9, "width": 282},
                {"height": 595, "id": 10, "number": 9, "width": 282},
                {"height": 496, "id": 11, "number": 10, "width": 139},
                {"height": 496, "id": 12, "number": 10, "width": 139},
                {"height": 496, "id": 13, "number": 11, "width": 567},
                {"height": 496, "id": 14, "number": 11, "width": 567},
                {"height": 170, "id": 15, "number": 12, "width": 709},
                {"height": 595, "id": 16, "number": 13, "width": 139},
                {"height": 595, "id": 17, "number": 14, "width": 229},
                {"height": 595, "id": 18, "number": 14, "width": 229},
                {"height": 595, "id": 19, "number": 15, "width": 102},
                {"height": 444, "id": 20, "number": 16, "width": 139},
                {"height": 444, "id": 21, "number": 17, "width": 567},
                {"height": 597, "id": 22, "number": 18, "width": 709},
                {"height": 495, "id": 23, "number": 19, "width": 139},
                {"height": 495, "id": 24, "number": 20, "width": 567},
                {"height": 320, "id": 25, "number": 21, "width": 890},
                {"height": 320, "id": 26, "number": 21, "width": 890},
                {"height": 330, "id": 27, "number": 22, "width": 890},
                {"height": 330, "id": 28, "number": 22, "width": 890},
                {"height": 300, "id": 29, "number": 23, "width": 1380},
                {"height": 120, "id": 30, "number": 24, "width": 709},
                {"height": 120, "id": 31, "number": 25, "width": 890},
                {"height": 596, "id": 32, "number": 26, "width": 1500},
                {"height": 596, "id": 33, "number": 27, "width": 2215},
                {"height": 596, "id": 34, "number": 28, "width": 715},
                {"height": 596, "id": 35, "number": 28, "width": 715},
                {"height": 66, "id": 36, "number": 29, "width": 709},
                {"height": 74, "id": 37, "number": 30, "width": 709},
                {"height": 56, "id": 38, "number": 31, "width": 709},
                {"height": 74, "id": 39, "number": 32, "width": 709},
                {"height": 140, "id": 40, "number": 33, "width": 1740},
                {"height": 160, "id": 41, "number": 34, "width": 555},
                {"height": 160, "id": 42, "number": 34, "width": 555},
                {"height": 140, "id": 43, "number": 35, "width": 895},
                {"height": 140, "id": 44, "number": 36, "width": 1279},
                {"height": 140, "id": 45, "number": 37, "width": 775}
            ],
            "stock": [
                {"height": 2070, "id": 0, "number": 1, "width": 2800},
                {"height": 2070, "id": 1, "number": 1, "width": 2800},
                {"height": 2070, "id": 2, "number": 1, "width": 2800},
                {"height": 2070, "id": 3, "number": 1, "width": 2800},
                {"height": 2070, "id": 4, "number": 1, "width": 2800}
            ]
        }
        """;

    @Test
    @DisplayName("Should process 46-piece dataset without rotation via /api/cut_1")
    void shouldProcessLargeDataset_WithoutRotation() throws Exception {
      mockMvc.perform(post("/api/cut_1")
          .contentType(MediaType.APPLICATION_JSON)
          .content(datasetJson))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.placedPieces").isArray())
          .andExpect(jsonPath("$.stock").isArray())
          .andExpect(jsonPath("$.waste").isArray());
    }

    @Test
    @DisplayName("Should process 46-piece dataset with rotation via /api/cut_2")
    void shouldProcessLargeDataset_WithRotation() throws Exception {
      mockMvc.perform(post("/api/cut_2")
          .contentType(MediaType.APPLICATION_JSON)
          .content(datasetJson))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.placedPieces").isArray())
          .andExpect(jsonPath("$.stock").isArray())
          .andExpect(jsonPath("$.waste").isArray());
    }
  }
}
