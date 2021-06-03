# Changelog

### Version 1.16.5 1.2.4
 - fix bug where configs were not synced properly to the client on the first server start

### Version 1.16.5 - 1.2.3
 - added translation ru_ru

### Version 1.16.5 - 1.2.2
 - allow default item worth to be zero

### Version 1.16.5 - 1.2.1
 - allow item values to be zero
 - added new config-option : enchantmentWhitelist
     - is by default empty
     - if it contains enchantments, all enchantments not listed in here are blacklisted
     - this option will deactivate the enchantmentBlacklist
 - added new config-option : globalEnchantmentCap
     - if set to a value higher than zero all enchantments are capped at this value
     - this option will deactivate the list enchantmentCaps

### Version 1.16.5 - 1.2.0
 - **clear your toollevleing table before updating, or you will loose all items that are stored in it**
 - support for 1.16.5
 - extended the command 'superenchant'
     - works now similar to 'enchant', but with no limits to the level
     - allows also enchantments that were previous not allowed together (e.g. silktouch & fortune on pickaxe)
     - requieres permission level 3
 - updated gui
     - more items allowed as payment
     - different items are having different worth
     - if you insert an item that has more worth than required, the extra points are going to be stored in the table for future usage
 - new formula to determine the payment cost
     - cheaper at lower levels then before
     - gets exponential more expensive in the higher levels
 - new config structure

### Version 1.16.2 - 1.1.1
 - support 1.16.4
 - fix not using payment slot 3 & 4

### Version 1.16.2 - 1.1.0
 - finished design for the leveling table
 - higher enchantment level -> higher cost for upgrading
 - set the payment item to netherite ingots, to prevent the creation of overpowered items
     - can be changed in the config
 - added tooltips in the gui

### Version 1.16.2 - 1.0.2
 - added caps to enchantments that will break when leveled to high

### Version 1.16.2 - 1.0.1
 - added enchantment blacklist
 - changed payment item
     - for armor/tools -> their repair material
     - everything else -> diamonds
     
### Version 1.16.2 - 1.0.0
 - initial release
