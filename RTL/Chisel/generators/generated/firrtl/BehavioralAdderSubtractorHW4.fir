FIRRTL version 3.3.0
circuit BehavioralAdderSubtractorHW4 :%[[
  {
    "class":"firrtl.transforms.DedupGroupAnnotation",
    "target":"~BehavioralAdderSubtractorHW4|BehavioralAdderSubtractor",
    "group":"BehavioralAdderSubtractor"
  },
  {
    "class":"firrtl.transforms.DedupGroupAnnotation",
    "target":"~BehavioralAdderSubtractorHW4|BehavioralAdderSubtractorHW4",
    "group":"BehavioralAdderSubtractorHW4"
  }
]]
  module BehavioralAdderSubtractor : @[src/main/scala/scabook/addersubtractors/BehavioralAdderSubtractor.scala 8:7]
    input clock : Clock @[src/main/scala/scabook/addersubtractors/BehavioralAdderSubtractor.scala 8:7]
    input reset : Reset @[src/main/scala/scabook/addersubtractors/BehavioralAdderSubtractor.scala 8:7]
    output io : { flip a : UInt<4>, flip b : UInt<4>, flip subtract : UInt<1>, result : UInt<4>} @[src/main/scala/scabook/addersubtractors/BehavioralAdderSubtractor.scala 10:14]

    node _fullResult_T = bits(io.subtract, 0, 0) @[src/main/scala/scabook/addersubtractors/BehavioralAdderSubtractor.scala 18:36]
    node _fullResult_T_1 = sub(io.a, io.b) @[src/main/scala/scabook/addersubtractors/BehavioralAdderSubtractor.scala 18:49]
    node _fullResult_T_2 = tail(_fullResult_T_1, 1) @[src/main/scala/scabook/addersubtractors/BehavioralAdderSubtractor.scala 18:49]
    node _fullResult_T_3 = add(io.a, io.b) @[src/main/scala/scabook/addersubtractors/BehavioralAdderSubtractor.scala 18:62]
    node _fullResult_T_4 = tail(_fullResult_T_3, 1) @[src/main/scala/scabook/addersubtractors/BehavioralAdderSubtractor.scala 18:62]
    node fullResult = mux(_fullResult_T, _fullResult_T_2, _fullResult_T_4) @[src/main/scala/scabook/addersubtractors/BehavioralAdderSubtractor.scala 18:23]
    node _io_result_T = bits(fullResult, 3, 0) @[src/main/scala/scabook/addersubtractors/BehavioralAdderSubtractor.scala 21:26]
    connect io.result, _io_result_T @[src/main/scala/scabook/addersubtractors/BehavioralAdderSubtractor.scala 21:13]

  module BehavioralAdderSubtractorHW4 : @[src/main/scala/scabook/addersubtractors/BehavioralAdderSubtractorHW4.scala 8:7]
    input clock : Clock @[src/main/scala/scabook/addersubtractors/BehavioralAdderSubtractorHW4.scala 8:7]
    input reset : UInt<1> @[src/main/scala/scabook/addersubtractors/BehavioralAdderSubtractorHW4.scala 8:7]
    output io : { flip a : UInt<4>, flip b : UInt<4>, flip subtract : UInt<1>, sum : UInt<4>} @[src/main/scala/scabook/addersubtractors/BehavioralAdderSubtractorHW4.scala 9:14]

    inst io_sum_module of BehavioralAdderSubtractor @[src/main/scala/scabook/addersubtractors/BehavioralAdderSubtractorHW.scala 28:24]
    connect io_sum_module.clock, clock
    connect io_sum_module.reset, reset
    connect io_sum_module.io.a, io.a @[src/main/scala/scabook/addersubtractors/BehavioralAdderSubtractorHW.scala 29:17]
    connect io_sum_module.io.b, io.b @[src/main/scala/scabook/addersubtractors/BehavioralAdderSubtractorHW.scala 30:17]
    connect io_sum_module.io.subtract, io.subtract @[src/main/scala/scabook/addersubtractors/BehavioralAdderSubtractorHW.scala 31:24]
    connect io.sum, io_sum_module.io.result @[src/main/scala/scabook/addersubtractors/BehavioralAdderSubtractorHW4.scala 17:10]
