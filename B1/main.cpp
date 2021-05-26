#include "PNC.h"
#include<iostream>
using namespace std;

int main() {
  int n, r;
  cout << "Enter n: ";
  cin >> n;
  cout << "Enter r: ";
  cin >> r;
  cout << combinations(n, r) << endl;
  return 0;
}