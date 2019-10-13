# java-tftfb

Build font raster files for MCU connected TFT displays.

Output is row-major, bits are MSB first. Characters and can be rotated and flipped.

Bit patterns are generated as inline comments, unused bits are marked with 'x'.

## Example output for 'R', rotated by 90°

    0x00,  0x00, // ..............xx
    0x1F,  0xF0, // ...#########..xx
    0x01,  0x10, // .......#...#..xx
    0x01,  0x10, // .......#...#..xx
    0x01,  0x10, // .......#...#..xx
    0x07,  0x10, // .....###...#..xx
    0x18,  0xE0, // ...##...###...xx
    0x00,  0x00, // ..............xx

## Example output for 'R', rotated by 90° and flipped

    0x00,  0x00, // ............xxxx
    0x7F,  0xC0, // .#########..xxxx
    0x44,  0x00, // .#...#......xxxx
    0x44,  0x00, // .#...#......xxxx
    0x44,  0x00, // .#...#......xxxx
    0x47,  0x00, // .#...###....xxxx
    0x38,  0xC0, // ..###...##..xxxx
    0x00,  0x00, // ............xxxx
