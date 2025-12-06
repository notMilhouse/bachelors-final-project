import numpy as np
from numpy.polynomial import polynomial as P
import matplotlib.pyplot as plt

# Your collected data
raw_values = [46623, 46680, 47220, 47175, 48030, 47300, 48500, 48790, 48860]  # example raw ADC values
weights = [0, 34, 310, 427, 630, 763, 973, 1284, 1579]  # corresponding weights in grams

# Fit polynomial (degree 2 = quadratic, try 2-3)
coefficients = np.polyfit(raw_values, weights, 2)
print("Coefficients (highest degree first):", coefficients)

# This gives you: weight = c[0]*x² + c[1]*x + c[2]
# For degree 2: [a2, a1, a0]

# Reverse order for easier reading
coefficients_reversed = coefficients[::-1]
print("Coefficients (constant first):", coefficients_reversed)
# Now: weight = coefficients_reversed[0] + coefficients_reversed[1]*x + coefficients_reversed[2]*x²

# Verify fit
fitted_weights = np.polyval(coefficients, raw_values)
print("Original weights:", weights)
print("Fitted weights:", fitted_weights)

# Plot to visualize
plt.scatter(raw_values, weights, label='Collected data')
x_fit = np.linspace(min(raw_values), max(raw_values), 100)
y_fit = np.polyval(coefficients, x_fit)
plt.plot(x_fit, y_fit, label='Polynomial fit', color='red')
plt.xlabel('Raw HX711 Value')
plt.ylabel('Weight (g)')
plt.legend()

plt.savefig('polynomial_fit.png', dpi=300, bbox_inches='tight')
