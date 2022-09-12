package org.moon.figura.lua.api.event;

import org.moon.figura.lua.LuaWhitelist;
import org.moon.figura.lua.docs.LuaFieldDoc;
import org.moon.figura.lua.docs.LuaMetamethodDoc;
import org.moon.figura.lua.docs.LuaTypeDoc;

@LuaWhitelist
@LuaTypeDoc(name = "EventsAPI", value = "events")
public class EventsAPI {

	public EventsAPI() {
		ENTITY_INIT = new LuaEvent();
		TICK = new LuaEvent();
		WORLD_TICK = new LuaEvent();
		RENDER = new LuaEvent();
		POST_RENDER = new LuaEvent();
		WORLD_RENDER = new LuaEvent();
		POST_WORLD_RENDER = new LuaEvent();
		CHAT_SEND_MESSAGE = new LuaEvent();
		CHAT_RECEIVE_MESSAGE = new LuaEvent();
		SKULL_RENDER = new LuaEvent();
		MOUSE_SCROLL = new LuaEvent();
		PREVIEW_RENDER = new LuaEvent();
		POST_PREVIEW_RENDER = new LuaEvent();
		CUSTOM_EVENT = new LuaEvent();
	}

	//Unsure on how to do the docs for these fields. Maybe we keep the @LuaFieldDoc, just don't allow them to be
	//whitelisted and accessed automatically?
	//Maybe in the __index comment we give a docs list of the events?

	@LuaWhitelist
	@LuaFieldDoc("events.entity_init")
	public final LuaEvent ENTITY_INIT;
	@LuaWhitelist
	@LuaFieldDoc("events.tick")
	public final LuaEvent TICK;
	@LuaWhitelist
	@LuaFieldDoc("events.world_tick")
	public final LuaEvent WORLD_TICK;
	@LuaWhitelist
	@LuaFieldDoc("events.render")
	public final LuaEvent RENDER;
	@LuaWhitelist
	@LuaFieldDoc("events.post_render")
	public final LuaEvent POST_RENDER;
	@LuaWhitelist
	@LuaFieldDoc("events.world_render")
	public final LuaEvent WORLD_RENDER;
	@LuaWhitelist
	@LuaFieldDoc("events.post_world_render")
	public final LuaEvent POST_WORLD_RENDER;
	@LuaWhitelist
	@LuaFieldDoc("events.chat_send_message")
	public final LuaEvent CHAT_SEND_MESSAGE;
	@LuaWhitelist
	@LuaFieldDoc("events.chat_receive_message")
	public final LuaEvent CHAT_RECEIVE_MESSAGE;
	@LuaWhitelist
	@LuaFieldDoc("events.skull_render")
	public final LuaEvent SKULL_RENDER;
	@LuaWhitelist
	@LuaFieldDoc("events.mouse_scroll")
	public final LuaEvent MOUSE_SCROLL;
	@LuaWhitelist
	@LuaFieldDoc("events.preview_render")
	public final LuaEvent PREVIEW_RENDER;
	@LuaWhitelist
	@LuaFieldDoc("events.post_preview_render")
	public final LuaEvent POST_PREVIEW_RENDER;
	@LuaWhitelist
	@LuaFieldDoc("events.custom")
	public final LuaEvent CUSTOM_EVENT;

	@LuaWhitelist
	@LuaMetamethodDoc(overloads = @LuaMetamethodDoc.LuaMetamethodOverload(types = {LuaEvent.class, EventsAPI.class, String.class}, comment = "events.__index.comment1"))
	public Object __index(String key) {
		if (key == null) return null;
		return switch (key) {
			case "ENTITY_INIT" -> ENTITY_INIT;
			case "TICK" -> TICK;
			case "WORLD_TICK" -> WORLD_TICK;
			case "RENDER" -> RENDER;
			case "POST_RENDER" -> POST_RENDER;
			case "WORLD_RENDER" -> WORLD_RENDER;
			case "POST_WORLD_RENDER" -> POST_WORLD_RENDER;
			case "CHAT_SEND_MESSAGE" -> CHAT_SEND_MESSAGE;
			case "CHAT_RECEIVE_MESSAGE" -> CHAT_RECEIVE_MESSAGE;
			case "SKULL_RENDER" -> SKULL_RENDER;
			case "MOUSE_SCROLL" -> MOUSE_SCROLL;
			case "PREVIEW_RENDER" -> PREVIEW_RENDER;
			case "POST_PREVIEW_RENDER" -> POST_PREVIEW_RENDER;
			case "CUSTOM_EVENT" -> CUSTOM_EVENT;
			default -> null;
		};
	}

	@Override
	public String toString() {
		return "EventsAPI";
	}
}
