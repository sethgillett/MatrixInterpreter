# MatrixInterpreter
A command line program that can perform matrix and scalar algebra and basic matrix operations.

**Current State:**

Scalar and matrix algrebra is possible, basic control statements (if statement, for loop, while loop) have been added. Scalars are stored to arbitrary precision.

**TODO:**

Make control statements count openers (if, for, while, def) and enders (end, return), and stop reading statements only once they net to 0.

**Example Code:**
```python
>>> a = (5+3)*2^4
>>> a
128
>>> b = 0
>>> while b < 5:
      a = a / 2.000
      b = b - 1
      end
>>> a
4.000
```

Or, using matrices:
```python
>>> A = 
[1 2
[3 4
>>> B =
[5 6
[7 8
>>> A*B
[20   30]
[50   50]
>>> A+B
[6    8 ]
[10   10]
```
Scalars are set to arbitrary length precision, however exponentiation only yields double precision.
