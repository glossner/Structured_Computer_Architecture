FIRRTL version 3.3.0
circuit SevenSegmentDisplay :%[[
  {
    "class":"firrtl.transforms.DedupGroupAnnotation",
    "target":"~SevenSegmentDisplay|SevenSegmentDisplay",
    "group":"SevenSegmentDisplay"
  }
]]
  module SevenSegmentDisplay : @[src/main/scala/scabook/SevenSegmentDisplay.scala 8:7]
    input clock : Clock @[src/main/scala/scabook/SevenSegmentDisplay.scala 8:7]
    input reset : UInt<1> @[src/main/scala/scabook/SevenSegmentDisplay.scala 8:7]
    output io : { flip binIn : UInt<4>, segOut : UInt<7>} @[src/main/scala/scabook/SevenSegmentDisplay.scala 9:14]

    node _B0_T = bits(io.binIn, 0, 0) @[src/main/scala/scabook/SevenSegmentDisplay.scala 15:34]
    wire B0 : UInt<1> @[src/main/scala/scabook/SevenSegmentDisplay.scala 15:25]
    connect B0, _B0_T @[src/main/scala/scabook/SevenSegmentDisplay.scala 15:25]
    node _B1_T = bits(io.binIn, 1, 1) @[src/main/scala/scabook/SevenSegmentDisplay.scala 16:34]
    wire B1 : UInt<1> @[src/main/scala/scabook/SevenSegmentDisplay.scala 16:25]
    connect B1, _B1_T @[src/main/scala/scabook/SevenSegmentDisplay.scala 16:25]
    node _B2_T = bits(io.binIn, 2, 2) @[src/main/scala/scabook/SevenSegmentDisplay.scala 17:34]
    wire B2 : UInt<1> @[src/main/scala/scabook/SevenSegmentDisplay.scala 17:25]
    connect B2, _B2_T @[src/main/scala/scabook/SevenSegmentDisplay.scala 17:25]
    node _B3_T = bits(io.binIn, 3, 3) @[src/main/scala/scabook/SevenSegmentDisplay.scala 18:34]
    wire B3 : UInt<1> @[src/main/scala/scabook/SevenSegmentDisplay.scala 18:25]
    connect B3, _B3_T @[src/main/scala/scabook/SevenSegmentDisplay.scala 18:25]
    node _a_T = bits(io.segOut, 6, 6) @[src/main/scala/scabook/SevenSegmentDisplay.scala 21:34]
    wire a : UInt<1> @[src/main/scala/scabook/SevenSegmentDisplay.scala 21:24]
    connect a, _a_T @[src/main/scala/scabook/SevenSegmentDisplay.scala 21:24]
    node _b_T = bits(io.segOut, 5, 5) @[src/main/scala/scabook/SevenSegmentDisplay.scala 22:34]
    wire b : UInt<1> @[src/main/scala/scabook/SevenSegmentDisplay.scala 22:24]
    connect b, _b_T @[src/main/scala/scabook/SevenSegmentDisplay.scala 22:24]
    node _c_T = bits(io.segOut, 4, 4) @[src/main/scala/scabook/SevenSegmentDisplay.scala 23:34]
    wire c : UInt<1> @[src/main/scala/scabook/SevenSegmentDisplay.scala 23:24]
    connect c, _c_T @[src/main/scala/scabook/SevenSegmentDisplay.scala 23:24]
    node _d_T = bits(io.segOut, 3, 3) @[src/main/scala/scabook/SevenSegmentDisplay.scala 24:34]
    wire d : UInt<1> @[src/main/scala/scabook/SevenSegmentDisplay.scala 24:24]
    connect d, _d_T @[src/main/scala/scabook/SevenSegmentDisplay.scala 24:24]
    node _e_T = bits(io.segOut, 2, 2) @[src/main/scala/scabook/SevenSegmentDisplay.scala 25:34]
    wire e : UInt<1> @[src/main/scala/scabook/SevenSegmentDisplay.scala 25:24]
    connect e, _e_T @[src/main/scala/scabook/SevenSegmentDisplay.scala 25:24]
    node _f_T = bits(io.segOut, 1, 1) @[src/main/scala/scabook/SevenSegmentDisplay.scala 26:34]
    wire f : UInt<1> @[src/main/scala/scabook/SevenSegmentDisplay.scala 26:24]
    connect f, _f_T @[src/main/scala/scabook/SevenSegmentDisplay.scala 26:24]
    node _g_T = bits(io.segOut, 0, 0) @[src/main/scala/scabook/SevenSegmentDisplay.scala 27:34]
    wire g : UInt<1> @[src/main/scala/scabook/SevenSegmentDisplay.scala 27:24]
    connect g, _g_T @[src/main/scala/scabook/SevenSegmentDisplay.scala 27:24]
    connect io.segOut, UInt<7>(0h0) @[src/main/scala/scabook/SevenSegmentDisplay.scala 42:15]
    node _T = eq(io.binIn, UInt<1>(0h0)) @[src/main/scala/scabook/SevenSegmentDisplay.scala 44:26]
    when _T : @[src/main/scala/scabook/SevenSegmentDisplay.scala 44:35]
      connect io.segOut, UInt<7>(0h7e) @[src/main/scala/scabook/SevenSegmentDisplay.scala 44:47]
    else :
      node _T_1 = eq(io.binIn, UInt<1>(0h1)) @[src/main/scala/scabook/SevenSegmentDisplay.scala 45:26]
      when _T_1 : @[src/main/scala/scabook/SevenSegmentDisplay.scala 45:35]
        connect io.segOut, UInt<7>(0h30) @[src/main/scala/scabook/SevenSegmentDisplay.scala 45:47]
      else :
        node _T_2 = eq(io.binIn, UInt<2>(0h2)) @[src/main/scala/scabook/SevenSegmentDisplay.scala 46:26]
        when _T_2 : @[src/main/scala/scabook/SevenSegmentDisplay.scala 46:35]
          connect io.segOut, UInt<7>(0h6d) @[src/main/scala/scabook/SevenSegmentDisplay.scala 46:47]
        else :
          node _T_3 = eq(io.binIn, UInt<2>(0h3)) @[src/main/scala/scabook/SevenSegmentDisplay.scala 47:26]
          when _T_3 : @[src/main/scala/scabook/SevenSegmentDisplay.scala 47:35]
            connect io.segOut, UInt<7>(0h79) @[src/main/scala/scabook/SevenSegmentDisplay.scala 47:47]
          else :
            node _T_4 = eq(io.binIn, UInt<3>(0h4)) @[src/main/scala/scabook/SevenSegmentDisplay.scala 48:26]
            when _T_4 : @[src/main/scala/scabook/SevenSegmentDisplay.scala 48:35]
              connect io.segOut, UInt<7>(0h33) @[src/main/scala/scabook/SevenSegmentDisplay.scala 48:47]
            else :
              node _T_5 = eq(io.binIn, UInt<3>(0h5)) @[src/main/scala/scabook/SevenSegmentDisplay.scala 49:26]
              when _T_5 : @[src/main/scala/scabook/SevenSegmentDisplay.scala 49:35]
                connect io.segOut, UInt<7>(0h5b) @[src/main/scala/scabook/SevenSegmentDisplay.scala 49:47]
              else :
                node _T_6 = eq(io.binIn, UInt<3>(0h6)) @[src/main/scala/scabook/SevenSegmentDisplay.scala 50:26]
                when _T_6 : @[src/main/scala/scabook/SevenSegmentDisplay.scala 50:35]
                  connect io.segOut, UInt<7>(0h5f) @[src/main/scala/scabook/SevenSegmentDisplay.scala 50:47]
                else :
                  node _T_7 = eq(io.binIn, UInt<3>(0h7)) @[src/main/scala/scabook/SevenSegmentDisplay.scala 51:26]
                  when _T_7 : @[src/main/scala/scabook/SevenSegmentDisplay.scala 51:35]
                    connect io.segOut, UInt<7>(0h70) @[src/main/scala/scabook/SevenSegmentDisplay.scala 51:47]
                  else :
                    node _T_8 = eq(io.binIn, UInt<4>(0h8)) @[src/main/scala/scabook/SevenSegmentDisplay.scala 52:26]
                    when _T_8 : @[src/main/scala/scabook/SevenSegmentDisplay.scala 52:35]
                      connect io.segOut, UInt<7>(0h7f) @[src/main/scala/scabook/SevenSegmentDisplay.scala 52:47]
                    else :
                      node _T_9 = eq(io.binIn, UInt<4>(0h9)) @[src/main/scala/scabook/SevenSegmentDisplay.scala 53:26]
                      when _T_9 : @[src/main/scala/scabook/SevenSegmentDisplay.scala 53:35]
                        connect io.segOut, UInt<7>(0h7b) @[src/main/scala/scabook/SevenSegmentDisplay.scala 53:47]

