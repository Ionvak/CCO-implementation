2025-07-01
19:17

==index:==
==1. [[#Abstract]]==
==2. [[#Model description]]==
==2.1 [[#Segments]]==
==2.2 [[#Perfusion area]]==
==2.3 [[#Boundary conditions]]==
==3. [[#Optimization criteria/Target functions/Model requirements]]==
==4. [[#Algorithm description]]==
==4.1 [[#Algorithm 1 - comparison of entire trees]]==
==4.2 [[#Algorithm 2 - step-wise optimization and comparison of individual bifurcations]]==

---

## Abstract:
The task is to build an algorithm able to create a simulation of the arterial topography within the heart that is accurate enough for practical purposes. This simulation works in a step-wise fashion, optimizing several criteria in each individual step, and in the total result of the simulation.

---
## Model description:

### Segments:
The arterial tree is viewed as a set of many interconnected segments (geometrically represented as straight cylindrical tubes). Each segment is fed by one parent segment and feeds two daughter segments. The tree starts at a root segment (called $iroot$) for which there is no parent segment ($B_{iroot} = NULL$), and ends in $N_{term}$ many terminal segments, for which there are no daughter segments ($D^l_i = D_i^r = NULL,\quad \beta^l(i) = \beta^r(i) = undefined$). All terminal segments are assumed to perfuse an equal area. Each segment is identified using 4 integer numbers:

1. It's unique segment index, or id ($i$).
2. A backward pointer holding the index of the parent segment ($B_i$).
3. One forward pointer holding the index of the left daughter segment ($D_i^l$).
4. One forward pointer holding the index of the right daughter segment ($D_i^r$).

Additionally, each segment has it's own length $l(i)$ and it's own radius $r(i)$. 

The location, orientation, and length of each segment is defined by the cartesian coordinates $x(i)$, $y(i)$ (in the case of 2D) of its distal end (the point just before bifurcation), together with the corresponding values of its parent segment (the only exception is the root segment).

Due to the binary mode of branching, the total number of segments can be calculated using:$$ N_{tot} = 2\cdot N_{term} -1 $$

### Perfusion area:
The area of tissue to be perfused into may be 2D or 3D, and could theoretically take on one of many shapes. It is initially considered as a 2D circular area for simplicity. This circular area is described using the perfusion area $A_{perf}$ and the perfusion radius $r_{perf}$.

### Boundary conditions:
The main feeding artery, represented by the root segment, is perfused at a pressure of $p_{perf}$, with a flow of $Q_{perf}$. 

---

## Optimization criteria/Target functions/Model requirements:

1. The bifurcation rule:$$ r^\gamma(i) = (r(D^l_i))^\gamma+(r(D^r_i))^\gamma$$
   Where:
   - $r(i)$ represents the radius of the (parent) segment $i$.
   - $\gamma$ is the bifurcation exponent.


2. Bifurcation ratios:$$ \beta^l(i) = \frac{r(D_i^l)}{r(i)} $$ $$ \beta^r(i) = \frac{r(D_i^r)}{r(i)} $$
   Where:
   - $r(i)$ represents the radius of the (parent) segment $i$.
   - $\beta^l(i)$ is the left bifurcation ratio.
   - $\beta^r(i)$ is the right bifurcation ratio.
   - $\beta < 1$
   - These ratio may be equal (symmetric bifurcation) or significantly different (asymmetric bifurcation).


3. Total blood volume:
   The total blood volume within the tree needs to be minimized:$$ T = \Sigma_{\forall i}l(i)\pi r(i)^2 $$
   Where:
   - T is the total blood volume within the tree.
   - $l(i)$ is the length of the segment $i$.
   - $r(i)$ is the radius of the segment $i$.

4. Perfusion area:
   The total perfusion flow should be finally distributed evenly over the perfusion area:$$ Q_i = \frac{Q_{perf}}{N_{term}} $$
	All individual perfusion areas should be supplied with equal flows at equal pressure:
   
	The pressure drop:
	The drop in pressure along the path from the root to any of the terminal segments should be the same.
	
	The flow:
	The flow $Q_i$ through a segment is proportional to  the number of individual distal perfusion areas connected to it:$$ Q_i = NDIST_i \cdot Q_{term} $$
	Note: $NDIST_i$ = 1 for terminal segments by definition.
 

5. Poiseuille's law:$$ Q = \frac{\pi \Delta Pr^4}{8\eta l} $$
   Where:
   - $Q$ is the flow rate.
   - $\Delta P$ is the drop in pressure.
   - $r$ is the radius.
   - $\eta$ is the fluid viscosity.
   - $l$ is the tubing length.


---

## Algorithm description:

### Algorithm 1 - comparison of entire trees:
This approach is relatively simple, but it is very limited, as it can only handle a small number of terminal segments. It's impracticality makes it largely unusable. 
1. Distribute the terminal segments over the perfusion area.
2. Generate and geometrically optimize every possible tree to obtain the minimal blood volume, given the distribution of terminal segments, one after the other.
3. Choose the tree with the minimal blood volume from the resulting trees.

### Algorithm 2 - step-wise generation, optimization, and comparison of individual bifurcations:
This approach is relatively more complex and involves more steps, but is able to handle a significantly larger number of terminal segments with a much lower computational load.

##### Process variables:
- The supporting circle is a subset of the total perfusion area for all the terminal segments in which the tree is contained and extended in each iteration. It is initialized to support one individual terminal perfusion area, and is increased in each iteration to support one more instance of such area. This process ensures an even distribution of terminal segments over the whole perfusion area.
- $r_{supp}$ is the radius of the supporting circle.
- $k_{term}$ is the number of terminal segments in the current iteration.
- $k_{tot}$ is the total number of segments in the current iteration.
- $(x(j),y(j))$ are the coordinates of the distal end of an already existing segment $j$.
- $(x(B_j),y(B_j))$ are the coordinates of the proximal end of an already existing segment $j$.
- $i_{conn}$ is the index of the pre-existing segment within the tree that we wish to connect the new segment to. This segment is cut in half in the process of bifurcation to allow for the geometric optimization of the new bifurcation. 
- $i_{old}$ is the index of the parent segment of $i_{conn}$ prior to adding the bifurcation.
- $i_{new}$ is the index of the new segment that we wish to add.
- $i_{bif}$ is the index of the segment that acts as the parent segment to both $i_{new}$ and $i_{conn}$ after adding the bifurcation. It connects the proximal end of $i_{new}$ and $i_{conn}$ to the distal end of $i_{old}$ It is originally the proximal half of the segment $i_{conn}$ prior to adding the bifurcation.
  ![[Pasted image 20250710154734.png]]
	Adding a terminal segment using a bifurcation. A pre-existing tree of $k_{term}$ segments is assumed to be balanced and scaled. Segment $i_{conn}$ is the selected site for the connection of the new terminal segment $i_{new}$. $i_{conn}$ is shortened (hollow section) and the new bifurcation $i_{bif}$ is inserted (solid section), and its coordinates are geometrically optimized (dashed section).

##### Initializing the tree:
1. Initialize the supporting circle to be the size of the perfusion area of a single terminal segment ($\pi\cdot r_{supp}^2 = \frac{A_{perf}}{N_{term}}$).
2. Initialize the root segment such that the proximal (feeding) end is at the circle border, and the distal (terminal) end is randomly within the supporting circle.
3. Using the length of the resulting segment, calculate the radius such that the resistance yields the flow $Q_{term}$.
4. $k_{term}$, $k_{tot}$ are set to 1.
##### Stretching the supporting circle:
5. The supporting circle is increased to accommodate one additional terminal perfusion area: $$\pi\cdot r_{supp}^2 = (k_{tot} + 1)\frac{A_{perf}}{N_{term}}$$
6. The segment coordinates are stretched to meet the increase in the supporting circle.
7. The segment radii are increased to meet the  increase in segment length in order to correct the total tree resistance.
##### Adding a terminal segment:
8. Initialize the threshold distance according to:$$ d_{thresh} = \sqrt{\frac{\pi \cdot r_{supp}^2}{k_{term}}} $$
9. A random location is selected for the distal end of the new segment within the supporting circle, with a uniform distribution.
10. Compute the distance from the new location to each segment. First compute the projection of the new location on the given segment:$$ d_{proj}(x,y,j) = \left( \begin{array}{cc} x(B_j) - x(j) \\ y(B_j) - y(j) \end{array} \right) \cdot \left( \begin{array}{cc} x - x(j) \\ y - y(j) \end{array} \right) \cdot l(j)^{-2} $$
	where:
   - "$\cdot$" denotes the dot product.
	If $0\leq d_{proj} \leq 1$ , the projection lies along the segment j. If the projection of the new location lies along the already existing segment $j$, calculate the orthogonal distance:$$ d_{ortho}(x,y,j) = 
   \left|\left( \begin{array}{cc} 		    - y(B_j) + y(j) \\ x(B_j) - x(j) \end{array} \right) \cdot \left( \begin{array}{cc} x - x(j) \\ y - y(j) \end{array}  \right)\right| \cdot l(j)^{-1} $$
    Else, calculate the distance between the randomly selected point, and one of the endpoints of the already existing segment $j$:$$ d_{end}(x,y,j) = Min\{ \sqrt{(x - x(j))^2+(y-y(j))^2}\text{ }, \sqrt{(x - x(B_j))^2+(y-y(B_j))^2} \} $$
11. If the distance thus obtained exceeds the threshold distance $d_{thresh}$, the point is selected to become the distal end of the new segment to be added. Else, the current selection is discarded and the random selection (tossing) is repeated. 
12. If the tossing fails $N_{toss}$ many times, reduce the threshold distance by 10% and try again. Repeat this until a point is selected. 
13. Choose one of the existing segments from among the relevant bifurcation candidates that were not already chosen as $i_{conn}$ in the current iteration, and and let it be $i_{conn}$.
14. Shorten $i_{conn}$ by half (the distal end being unaffected)
15. Insert $i_{bif}$ in place of the removed half, and connect $i_{new}$ and the remaining half of $i_{conn}$ to the distal end of it.
##### Rescaling the tree:
16. Re-adjust the flow in the segments $i_{new}$ and $i_{conn}$ such that $Q_{inew} = Q_{term}$ and $Q_{iconn} = NDIST_{iconn}\cdot Q_{term}$ via the ratio $\frac{r(i_{conn})}{r(i_{new})}$.
17. Re-adjust the flow in the segment $i_{bif}$ to be $Q_{ibif} = Q_{iconn} + Q_{term}$ .
18. Re-adjust the bifurcation ratios in all segments proximal to $i_{bif}$ recursively up to the root segment.
19. Re-adjust $r(iroot)$ to obtain a total flow within the tree of $(k_{term}+1)\cdot Q_{term}$.
20. Optimize the addition geometrically to obtain the minimum of the target function 
21. If the optimized addition to the tree does not intersect with any pre-existing segment (except $i_{bif}$ and $i_{conn}$), store the values of the target function and bifurcation coordinates and revert the tree to the state prior to the bifurcation; else discard the new bifurcation and revert the tree.
22. If all relevant candidates for the connection have been tried, continue; else return to step 13.
23. Adopt the bifurcation found to yield the lowest value of the target function.
24. increase $k_{term}$ by 1 and $k_{tot}$ by 2.
25. If there are $N_{term}$ many terminal segments, halt; else return to step 5.


























