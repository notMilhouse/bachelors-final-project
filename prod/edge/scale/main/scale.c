/*
 * ESP32 with HX711 - Weight Reading with Polynomial Regression
 * 
 * Uses polynomial coefficients from regression analysis to convert
 * raw HX711 values to weight in grams
 * 
 * Wiring:
 * Load Cell -> HX711 (Red→E+, Black→E-, White→A-, Green→A+)
 * HX711 to ESP32:
 *   VCC -> 3.3V
 *   GND -> GND
 *   DT  -> GPIO 15
 *   SCK -> GPIO 5
 */

#include <inttypes.h>
#include <esp_log.h>
#include <freertos/FreeRTOS.h>
#include <freertos/task.h>
#include <hx711.h>

static const char *TAG = "hx711-example";

// Polynomial coefficients from your regression analysis
// Replace these with your actual values from polyfit
// For degree 2: weight = a2*x^2 + a1*x + a0
// Using:  [ 4.65066733e-05 -3.85988172e+00  7.89429013e+04]
float a2 = 4.65066733e-05;           // Coefficient for x^2 (change this)
float a1 = -3.85988172e+00;      // Coefficient for x (change this)
float a0 = 7.89429013e+04;         // Constant term (change this)

// Function to calculate weight from raw value using polynomial
float rawToWeight(int32_t raw_value) {
  float x = (float)raw_value;
  float weight = a2 * x * x + a1 * x + a0;
  return weight;
}

void test(void *pvParameters) {
    hx711_t dev =
    {
        .dout = 15,
        .pd_sck = 5,
        .gain = HX711_GAIN_A_64
    };

    // initialize device
    ESP_ERROR_CHECK(hx711_init(&dev));
  
    // read from device
    while (1)
    {
        esp_err_t r = hx711_wait(&dev, 500);
        if (r != ESP_OK)
        {
            ESP_LOGE(TAG, "Device not found: %d (%s)\n", r, esp_err_to_name(r));
            continue;
        }

        int32_t data;
        r = hx711_read_average(&dev, 15, &data);
        if (r != ESP_OK)
        {
            ESP_LOGE(TAG, "Could not read data: %d (%s)\n", r, esp_err_to_name(r));
            continue;
        }
        float weight = rawToWeight(data);
        ESP_LOGI(TAG, "Raw data: %" PRIi32, data);
        ESP_LOGI(TAG, "Weight converted: %f", weight);

        vTaskDelay(pdMS_TO_TICKS(500));
    }
}

void app_main() {
    xTaskCreate(test, "test", configMINIMAL_STACK_SIZE * 5, NULL, 5, NULL);
}
