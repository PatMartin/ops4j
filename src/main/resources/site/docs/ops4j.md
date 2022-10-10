# Ops4J

Ops4J is a framework ...

## Option conventions

The following conventions should be applied so that 

### Operations

* Long and short names should be provided.

* Short names should be:
  
  * Prefaced by a single `-`
  
  * As short as possible
  
  * Single characters should always be used for options without arguments to allow support option clustering.
    
    * `-abc` is equivalent to `-a -b -c`

* Long names should be...
  
  * named to describe the option they represent.
  
  * prefaced by `--`, which allows support for short option clustering.
  
  * lower-case only
