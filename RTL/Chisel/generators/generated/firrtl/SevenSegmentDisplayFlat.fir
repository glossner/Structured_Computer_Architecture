FIRRTL version 3.3.0
circuit SevenSegmentDisplayFlat :%[[
  {
    "class":"firrtl.transforms.DedupGroupAnnotation",
    "target":"~SevenSegmentDisplayFlat|SevenSegmentDisplayFlat",
    "group":"SevenSegmentDisplayFlat"
  }
]]
  module SevenSegmentDisplayFlat : @[src/main/scala/scabook/SevenSegmentDisplayFlat.scala 6:7]
    input clock : Clock @[src/main/scala/scabook/SevenSegmentDisplayFlat.scala 6:7]
    input reset : UInt<1> @[src/main/scala/scabook/SevenSegmentDisplayFlat.scala 6:7]
    output io : { flip binIn_B0 : UInt<1>, flip binIn_B1 : UInt<1>, flip binIn_B2 : UInt<1>, flip binIn_B3 : UInt<1>, segOut_a : UInt<1>, segOut_b : UInt<1>, segOut_c : UInt<1>, segOut_d : UInt<1>, segOut_e : UInt<1>, segOut_f : UInt<1>, segOut_g : UInt<1>} @[src/main/scala/scabook/SevenSegmentDisplayFlat.scala 7:14]

    wire binIn : UInt<4> @[src/main/scala/scabook/SevenSegmentDisplayFlat.scala 25:19]
    node binIn_lo = cat(io.binIn_B1, io.binIn_B0) @[src/main/scala/scabook/SevenSegmentDisplayFlat.scala 26:15]
    node binIn_hi = cat(io.binIn_B3, io.binIn_B2) @[src/main/scala/scabook/SevenSegmentDisplayFlat.scala 26:15]
    node _binIn_T = cat(binIn_hi, binIn_lo) @[src/main/scala/scabook/SevenSegmentDisplayFlat.scala 26:15]
    connect binIn, _binIn_T @[src/main/scala/scabook/SevenSegmentDisplayFlat.scala 26:9]
    wire segOut : UInt<7> @[src/main/scala/scabook/SevenSegmentDisplayFlat.scala 29:20]
    node _io_segOut_a_T = bits(segOut, 6, 6) @[src/main/scala/scabook/SevenSegmentDisplayFlat.scala 30:24]
    connect io.segOut_a, _io_segOut_a_T @[src/main/scala/scabook/SevenSegmentDisplayFlat.scala 30:15]
    node _io_segOut_b_T = bits(segOut, 5, 5) @[src/main/scala/scabook/SevenSegmentDisplayFlat.scala 31:24]
    connect io.segOut_b, _io_segOut_b_T @[src/main/scala/scabook/SevenSegmentDisplayFlat.scala 31:15]
    node _io_segOut_c_T = bits(segOut, 4, 4) @[src/main/scala/scabook/SevenSegmentDisplayFlat.scala 32:24]
    connect io.segOut_c, _io_segOut_c_T @[src/main/scala/scabook/SevenSegmentDisplayFlat.scala 32:15]
    node _io_segOut_d_T = bits(segOut, 3, 3) @[src/main/scala/scabook/SevenSegmentDisplayFlat.scala 33:24]
    connect io.segOut_d, _io_segOut_d_T @[src/main/scala/scabook/SevenSegmentDisplayFlat.scala 33:15]
    node _io_segOut_e_T = bits(segOut, 2, 2) @[src/main/scala/scabook/SevenSegmentDisplayFlat.scala 34:24]
    connect io.segOut_e, _io_segOut_e_T @[src/main/scala/scabook/SevenSegmentDisplayFlat.scala 34:15]
    node _io_segOut_f_T = bits(segOut, 1, 1) @[src/main/scala/scabook/SevenSegmentDisplayFlat.scala 35:24]
    connect io.segOut_f, _io_segOut_f_T @[src/main/scala/scabook/SevenSegmentDisplayFlat.scala 35:15]
    node _io_segOut_g_T = bits(segOut, 0, 0) @[src/main/scala/scabook/SevenSegmentDisplayFlat.scala 36:24]
    connect io.segOut_g, _io_segOut_g_T @[src/main/scala/scabook/SevenSegmentDisplayFlat.scala 36:15]
    connect segOut, UInt<1>(0h0) @[src/main/scala/scabook/SevenSegmentDisplayFlat.scala 39:10]
    node _T = eq(binIn, UInt<1>(0h0)) @[src/main/scala/scabook/SevenSegmentDisplayFlat.scala 42:19]
    when _T : @[src/main/scala/scabook/SevenSegmentDisplayFlat.scala 42:28]
      connect segOut, UInt<7>(0h7e) @[src/main/scala/scabook/SevenSegmentDisplayFlat.scala 42:37]
    else :
      node _T_1 = eq(binIn, UInt<1>(0h1)) @[src/main/scala/scabook/SevenSegmentDisplayFlat.scala 43:19]
      when _T_1 : @[src/main/scala/scabook/SevenSegmentDisplayFlat.scala 43:28]
        connect segOut, UInt<6>(0h30) @[src/main/scala/scabook/SevenSegmentDisplayFlat.scala 43:37]
      else :
        node _T_2 = eq(binIn, UInt<2>(0h2)) @[src/main/scala/scabook/SevenSegmentDisplayFlat.scala 44:19]
        when _T_2 : @[src/main/scala/scabook/SevenSegmentDisplayFlat.scala 44:28]
          connect segOut, UInt<7>(0h6d) @[src/main/scala/scabook/SevenSegmentDisplayFlat.scala 44:37]
        else :
          node _T_3 = eq(binIn, UInt<2>(0h3)) @[src/main/scala/scabook/SevenSegmentDisplayFlat.scala 45:19]
          when _T_3 : @[src/main/scala/scabook/SevenSegmentDisplayFlat.scala 45:28]
            connect segOut, UInt<7>(0h79) @[src/main/scala/scabook/SevenSegmentDisplayFlat.scala 45:37]
          else :
            node _T_4 = eq(binIn, UInt<3>(0h4)) @[src/main/scala/scabook/SevenSegmentDisplayFlat.scala 46:19]
            when _T_4 : @[src/main/scala/scabook/SevenSegmentDisplayFlat.scala 46:28]
              connect segOut, UInt<6>(0h33) @[src/main/scala/scabook/SevenSegmentDisplayFlat.scala 46:37]
            else :
              node _T_5 = eq(binIn, UInt<3>(0h5)) @[src/main/scala/scabook/SevenSegmentDisplayFlat.scala 47:19]
              when _T_5 : @[src/main/scala/scabook/SevenSegmentDisplayFlat.scala 47:28]
                connect segOut, UInt<7>(0h5b) @[src/main/scala/scabook/SevenSegmentDisplayFlat.scala 47:37]
              else :
                node _T_6 = eq(binIn, UInt<3>(0h6)) @[src/main/scala/scabook/SevenSegmentDisplayFlat.scala 48:19]
                when _T_6 : @[src/main/scala/scabook/SevenSegmentDisplayFlat.scala 48:28]
                  connect segOut, UInt<7>(0h5f) @[src/main/scala/scabook/SevenSegmentDisplayFlat.scala 48:37]
                else :
                  node _T_7 = eq(binIn, UInt<3>(0h7)) @[src/main/scala/scabook/SevenSegmentDisplayFlat.scala 49:19]
                  when _T_7 : @[src/main/scala/scabook/SevenSegmentDisplayFlat.scala 49:28]
                    connect segOut, UInt<7>(0h70) @[src/main/scala/scabook/SevenSegmentDisplayFlat.scala 49:37]
                  else :
                    node _T_8 = eq(binIn, UInt<4>(0h8)) @[src/main/scala/scabook/SevenSegmentDisplayFlat.scala 50:19]
                    when _T_8 : @[src/main/scala/scabook/SevenSegmentDisplayFlat.scala 50:28]
                      connect segOut, UInt<7>(0h7f) @[src/main/scala/scabook/SevenSegmentDisplayFlat.scala 50:37]
                    else :
                      node _T_9 = eq(binIn, UInt<4>(0h9)) @[src/main/scala/scabook/SevenSegmentDisplayFlat.scala 51:19]
                      when _T_9 : @[src/main/scala/scabook/SevenSegmentDisplayFlat.scala 51:28]
                        connect segOut, UInt<7>(0h7b) @[src/main/scala/scabook/SevenSegmentDisplayFlat.scala 51:37]

