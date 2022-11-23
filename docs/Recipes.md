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


