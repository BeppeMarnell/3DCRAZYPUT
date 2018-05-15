Two path-finding algorithms.

1)Breadth-first search.
  The pseudo-code is taken from here: https://en.wikipedia.org/wiki/Breadth-first_search
  
  
  
2) A* algorithm.
  The pseudo-code is taken from here: https://en.wikipedia.org/wiki/A*_search_algorithm#Pseudocode
  
 Improvements for the next phase bot wise:
  -Add a 'turn cost' every time the algorithm makes a turn the cost rises too much, so
   the now we can take the path with less turns(it doesn't mean that it's the shortest).
  -Implement a GA.
  Important: We could also use multi-angel pathfinding which are based on A*.
      Here are some of them: Field D*
                             Theta*
                             BlockA*
  
