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


#if !defined(FIT_BUFFERED_RECORD_MESG_BROADCASTER_HPP)
#define FIT_BUFFERED_RECORD_MESG_BROADCASTER_HPP

#include <vector>
#include <list>
#include "fit_buffered_record_mesg_listener.hpp"
#include "fit_buffered_record_mesg.hpp"
#include "fit_record_mesg_listener.hpp"

using namespace std;

namespace fit
{

class BufferedRecordMesgBroadcaster : public RecordMesgListener
{
   public:
      BufferedRecordMesgBroadcaster(void);
      void AddListener(BufferedRecordMesgListener& mesgListener);
      void OnMesg(RecordMesg& mesg);

   private:
      BufferedRecordMesg bufferedRecordMesg;
      vector<BufferedRecordMesgListener*> listeners;
};

} // namespace fit

#endif // !defined(FIT_BUFFERED_RECORD_MESG_BROADCASTER_HPP)
