# Node Operation Recipes

# Data Conversion

| NODE-OPERATION | DESCRIPTION                                                                             |
| -------------- | --------------------------------------------------------------------------------------- |
| to:double      | Convert a node to a double node.                                                        |
| to:string      | Convert a node to a string node.<br/><br/>echo '{"pi":3.14}' \| map /pi=to:string:/pi   |
| to:month       | Convert a numeric to a month.<br/><br/>echo '{"month":6}' \| map /month=to:month:/month |
| to:float       | Convert a node to a float node.<br/><br/>echo '{"f":"3.14"}' \| map /f=to:float:/f      |
| to:int         |                                                                                         |
| to:upper       |                                                                                         |
| to:lower       |                                                                                         |

# Extracting Data

## Finding friends

```bash
$ map -D 1 /person-1=gen:person /person-2=gen:person | \
  map /matched='json:path(-x $.*.first)' -P
```

Which returns...

```javascript
{
  "matched" : [ "Kandice", "Kristal" ]
}
```
