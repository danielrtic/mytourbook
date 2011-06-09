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


#if !defined(FIT_EVENT_MESG_HPP)
#define FIT_EVENT_MESG_HPP

#include "fit_mesg.hpp"
#include "fit_mesg_with_event.hpp"

namespace fit
{

class EventMesg : public Mesg, public MesgWithEvent
{
   public:
      EventMesg(void) : Mesg(Profile::MESG_EVENT)
      {
      }

      EventMesg(const Mesg &mesg) : Mesg(mesg)
      {
      }

      ///////////////////////////////////////////////////////////////////////
      // Returns timestamp field
      // Units: s
      ///////////////////////////////////////////////////////////////////////
      FIT_DATE_TIME GetTimestamp(void)
      {
         return GetFieldUINT32Value(253);
      }

      ///////////////////////////////////////////////////////////////////////
      // Set timestamp field
      // Units: s
      ///////////////////////////////////////////////////////////////////////
      void SetTimestamp(FIT_DATE_TIME timestamp)
      {
         SetFieldUINT32Value(253, timestamp);
      }

      ///////////////////////////////////////////////////////////////////////
      // Returns event field
      ///////////////////////////////////////////////////////////////////////
      FIT_EVENT GetEvent(void)
      {
         return GetFieldENUMValue(0);
      }

      ///////////////////////////////////////////////////////////////////////
      // Set event field
      ///////////////////////////////////////////////////////////////////////
      void SetEvent(FIT_EVENT event)
      {
         SetFieldENUMValue(0, event);
      }

      ///////////////////////////////////////////////////////////////////////
      // Returns event_type field
      ///////////////////////////////////////////////////////////////////////
      FIT_EVENT_TYPE GetEventType(void)
      {
         return GetFieldENUMValue(1);
      }

      ///////////////////////////////////////////////////////////////////////
      // Set event_type field
      ///////////////////////////////////////////////////////////////////////
      void SetEventType(FIT_EVENT_TYPE eventType)
      {
         SetFieldENUMValue(1, eventType);
      }

      ///////////////////////////////////////////////////////////////////////
      // Returns data16 field
      ///////////////////////////////////////////////////////////////////////
      FIT_UINT16 GetData16(void)
      {
         return GetFieldUINT16Value(2);
      }

      ///////////////////////////////////////////////////////////////////////
      // Set data16 field
      ///////////////////////////////////////////////////////////////////////
      void SetData16(FIT_UINT16 data16)
      {
         SetFieldUINT16Value(2, data16);
      }

      ///////////////////////////////////////////////////////////////////////
      // Returns data field
      ///////////////////////////////////////////////////////////////////////
      FIT_UINT32 GetData(void)
      {
         return GetFieldUINT32Value(3);
      }

      ///////////////////////////////////////////////////////////////////////
      // Set data field
      ///////////////////////////////////////////////////////////////////////
      void SetData(FIT_UINT32 data)
      {
         SetFieldUINT32Value(3, data);
      }

      ///////////////////////////////////////////////////////////////////////
      // Returns event_group field
      ///////////////////////////////////////////////////////////////////////
      FIT_UINT8 GetEventGroup(void)
      {
         return GetFieldUINT8Value(4);
      }

      ///////////////////////////////////////////////////////////////////////
      // Set event_group field
      ///////////////////////////////////////////////////////////////////////
      void SetEventGroup(FIT_UINT8 eventGroup)
      {
         SetFieldUINT8Value(4, eventGroup);
      }

};

} // namespace fit

#endif // !defined(FIT_EVENT_MESG_HPP)
