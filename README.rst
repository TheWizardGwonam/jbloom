jbloom
======

``jbloom`` is a java port of the popular https://travis-ci.org/jaybaird/python-bloomfilter repo module
that includes a Bloom Filter data structure, an implementation of the Scalable Bloom Filter[1] and
an implementation of the Dynamic Bloom Filter[2].

Bloom filters are great if you understand what amount of bits you need to set
aside early to store your entire set. Scalable Bloom Filters allow your bloom
filter bits to grow as a function of false positive probability and size.
Dynamic Bloom Filters allow your bloom filters to grow like a Scalable
Bloom Filter, but they preserve the ability to intersect or union with
one another.

A filter is "full" when at capacity: M * ((ln 2 ^ 2) / abs(ln p)), where M
is the number of bits and p is the false positive probability. When capacity
is reached a new filter is then created exponentially larger than the last
with a tighter probability of false positives and a larger number of hash
functions.

installation
============
installation info to come

examples
========
examples and explanation can be found in src/jbloom/examples

references
==========
[1] P. Almeida, C.Baquero, N. Preguiça, D. Hutchison, Scalable Bloom Filters,
(GLOBECOM 2007), IEEE, 2007. http://www.sciencedirect.com/science/article/pii/S0020019006003127
[2] http://ieeexplore.ieee.org/document/4796196/?reload=true