#ifdef BUILD_PNC_DLL
  #define PNC_API __declspec(dllexport)
#else
  #define PNC_API __declspec(dllimport)
#endif
extern "C" PNC_API double combinations(int, int);