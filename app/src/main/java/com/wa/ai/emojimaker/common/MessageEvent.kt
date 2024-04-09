package com.wa.ai.emojimaker.common

class MessageEvent {
	var isBooleanValue = false

	var typeEvent: Int = -1
	var intTimeMax = 0
	var intTimeStart = 0
	var floatValue = 0f

	constructor(typeEvent: Int, intValue1: Int, intValue2: Int) {
		this.typeEvent = typeEvent
		this.intTimeMax = intValue1
		this.intTimeStart = intValue2
	}
	constructor(typeEvent: Int, booleanValue: Boolean) {
		this.typeEvent = typeEvent
		isBooleanValue = booleanValue
	}	constructor(typeEvent: Int) {
		this.typeEvent = typeEvent
	}

	constructor(typeEvent: Int, floatValue: Float) {
		this.typeEvent = typeEvent
		this.floatValue = floatValue
	}
}