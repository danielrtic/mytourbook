////////////////////////////////////////////////////////////////////////////////
// The following FIT Protocol software provided may be used with FIT protocol
// devices only and remains the copyrighted property of Dynastream Innovations Inc.
// The software is being provided on an "as-is" basis and as an accommodation,
// and therefore all warranties, representations, or guarantees of any kind
// (whether express, implied or statutory) including, without limitation,
// warranties of merchantability, non-infringement, or fitness for a particular
// purpose, are specifically disclaimed.
//
// Copyright 2008 Dynastream Innovations Inc.
////////////////////////////////////////////////////////////////////////////////
// ****WARNING****  This file is auto-generated!  Do NOT edit this file.
// Profile Version = 1.20Release
// Tag = $Name: AKW1_200 $
////////////////////////////////////////////////////////////////////////////////


#include "fit_crc.hpp"

namespace fit
{

FIT_UINT16 CRC::Get16(FIT_UINT16 crc, FIT_UINT8 byte) 
{
   static const FIT_UINT16 crc_table[16] = 
   {
      0x0000, 0xCC01, 0xD801, 0x1400, 0xF001, 0x3C00, 0x2800, 0xE401,
      0xA001, 0x6C00, 0x7800, 0xB401, 0x5000, 0x9C01, 0x8801, 0x4400 
   };
   FIT_UINT16 tmp;

   // compute checksum of lower four bits of byte 
   tmp = crc_table[crc & 0xF];
   crc  = (crc >> 4) & 0x0FFF;
   crc  = crc ^ tmp ^ crc_table[byte & 0xF];

   // now compute checksum of upper four bits of byte 
   tmp = crc_table[crc & 0xF];
   crc  = (crc >> 4) & 0x0FFF;
   crc  = crc ^ tmp ^ crc_table[(byte >> 4) & 0xF];

   return crc;
}

} // namespace fit
