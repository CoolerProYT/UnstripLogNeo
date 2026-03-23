### 1.1.1
- Added support for modded log that is not registered to STRIPPABLES field

### 1.1.0
- Added bark for each type of vanilla log/wood
- Added common config to handle undefined log/wood and 'bark' item to drop/use
  - `item` field is for defining the item id to drop/use as bark when stripping and unstripping
  -  `allowUnknownLog` field is to toggle whether log/wood that is not defined in `unstrip-detailed.json` can be unstripped 
- Added `unstrip-detailed` config to define drop and bark to use for each type of wood/log (Please visit the wiki for more detail)
- Added a config driven bark type, player/pack dev can add more bark type by adding texture and new field in `bark-type.json` (Please visit the wiki for more detail)

By the time this version is released, wiki might not ready yet, please wait for a few days if wiki is not ready yet.