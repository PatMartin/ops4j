# Recipes

Recipes are organized  into categorical subsections.

## Creating Data

## JDBC

### Inserting records

```bash
map -D 10 /s=seq | jdbc:insert -U postgres -p local1 -d ops \
  'insert into ops.msgs(seq,msg) values(${/s},'\''msg-${/s}'\'')'
```

## Mapping Data

```bash
map -D 1 /a/b=gen:person | map /input=/ /matched='json:path:/a/b(-x $..first)' -P
```

## Clustering

Group iris data into 3 clusters based upon everything but `species`.

```bash
cat iris.json | smile:cluster --omit species -c 3 | map /=/ /n='seq()' | http:server
```

## Clustering to DTree

For the purpose of explaining differences in data.

## USL Estimation

Given USL equation:

X(N)=NX(1)1+α(N−1)+βN(N−1)(1)

$$
X(N) = \frac{N \cdot X(1)}{1+α(N−1)+βN(N−1)}
$$

Given Throughput and response time we can backfill this equation as:

$$
RSP = \frac{CPS \cdot X(1)}{1+α(CPS−1)+βN(CPS−1)}
$$

Idea:

- Use polyfit to find coefficients to a USL form equation.
  
  - N =~ Calls per Second
    
    - Should the # of things which are communicating with one another.
      
      - Service/instance
      
      - Instance/Threads

Use millikan's method:

## Clustering

Testing the clustering algorithms versus standard clustering datasets.

```bash
cat data/clustering/spiral.txt | \
  perl -ne 'm/^\s*(\S+)\s+(\S+)/;print "{\"x\":$1,\"y\":$2}\n";' | \
  smile:cluster --spectral.clusters=4 --spectral.sigma=5.0 | \
  map /cluster='text(-i C${/cluster})' /x=/x /y=/y | \
  http:server
```
