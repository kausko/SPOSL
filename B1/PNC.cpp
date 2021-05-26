#include "PNC.h"

double combinations(int n, int r) {
  if (n-r < r)
    r = n-r;
  double nCr = 1;
  for (int i = 0; i < r; i++) {
    nCr *= (n - i);
    nCr /= (i + 1);
  }
  return nCr;
}