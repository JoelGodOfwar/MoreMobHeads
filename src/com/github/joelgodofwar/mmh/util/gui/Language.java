package com.github.joelgodofwar.mmh.util.gui;

import java.util.HashMap;
import java.util.Map;

public enum Language {
	CS_CZ("cs_CZ", "Czech", "čeština", "a2708119-1aac-4b07-a02c-032d8b073cf2",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTI2YTJhOTRkZjk2ZDE3YmM0OWJkYTRmMDIzZjY0MzRhYzI1OTcxZDIzZThlOGNiMDAxNDgxNzk3NzdiZjhmMSJ9fX0="),
	DE_DE("de_DE", "German", "Deutsch", "be211c23-d8aa-4119-bd0d-7f50fd115d9f",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWY4M2Y3M2I4NTZlNjU4NjZkZWFjMzkyZmEzNzJmOTg3OThkODljZGE2YmJlMDFkMWU3OTEyNTk3ZjkwMDViYSJ9fX0="),
	EN_US("en_US", "English", "English", "3c30484a-76d3-4cfe-88e5-e7599bc9ac4d",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDY2N2Q2NTRkNjQ0ZGQwYTZjMmViZjkzNTg0NGVkMzBhY2ZkMzE2YWMyMTI1NmU3YThlYzc3NjU4OTE0ZmY2ZiJ9fX0="),
	ES_ES("es_ES", "Spanish", "Español SP", "931ff46b-ec86-4456-9aa8-67fb39206c7c",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzAxNmEzNTg3NzRmZTU1YzY0ZGEzYmI2YmE2ZGM3Yzk3Mzc4ZWUzYWJiNWNmZGQ2ZDkwMDQxZGEzY2JlMWU4MSJ9fX0="),
	ES_MX("es_MX", "Spanish", "Español MX", "30b1fcca-a535-4b08-a284-414d62759da1",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjExOTAzMDQ4MTc0NTNmNDljZDIxNDI2NTFjOWUzMWM1M2QwZWViMjJjYjE2MmUyM2U2MzMzYTc3ZDU4ZWFhNyJ9fX0="),
	FR_FR("fr_FR", "French", "Français", "2ddda585-fd12-400f-a2d7-453ec59e2569",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjE5YzBhZDEwYWM3MmE3ZjgyMmU5YWNhMTZjMjliOTFkOGQ3ZGUzZTliMDQ5OTgzZDgzNDg0NjE4ZjdjYWRkZiJ9fX0="),
	HU_HU("hu_HU", "Hungarian", "magyar", "da06458d-0864-4d83-8ca7-8994886085e0",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzZhMTgyYjI1YjhjNDlmYzBmODE0NzQyNDU5ZmVmNzIxZDk3N2I3MzIxNDJjYjA1YzE2MjVhNmE1YmIzZDU5MyJ9fX0="),
	IT_IT("it_IT", "Italian", "Italiano", "a17baafd-e77e-4884-8c47-e7a08146147e",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjlmMTUxZDFjZWE3ZTY3MDcyM2M0OGQ3MGUzYWUwNWRkNTYxNDNiYzRkOTgxOWM2M2JiYTBhMThhYTUwOTI1YyJ9fX0="),
	JA_JP("ja_JP", "Japanese", "日本語", "20774ef9-f853-4246-8778-50b828007e58",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzVlYWE3ZDk3NjBlZDk0NDViMmJjMDI4Zjg5YzBjM2M3MDVlMWIwNjUzZTBjOTZmODIwMTdhYTk2YWE2YzRmYSJ9fX0="),
	KO_KR("ko_KR", "Korean", "한국어", "7ea086c2-48a5-4b91-8f71-7153bb6e904f",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjBjZjRkOThkYjcwM2ZjNTE1MWQ1MTA0NDIwNmFmYTNhZGEwNjBmYzAzZTc5Njg1ODFhOWNmOWIzYWQ1NGM4YyJ9fX0="),
	LOL_US("lol_US", "LolCat", "LolCat", "f0aaa05b-0283-4663-9b57-52dbf2ca2750",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWU4OTI1MzlhNTFjODc2OWE1ZjUyNDMxYzU3ZDhiMmNlMjFkOTRhNzEwMDZkNDY2ZjYwMjBhYjIyN2ZjMzgyNSJ9fX0="),
	MY_MY("my_MY", "Malay", "Melayu", "21409746-749e-4f90-812e-6ca881355111",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWQ2YjhlMmRmMDY4ODQxNTIzMDJiOWNiZmZkNzc1N2IxYmU3ZDVjZjgwOWIyNDlmNzUwYjJkODFjMDU1ZjMxNyJ9fX0="),
	NL_NL("nl_NL", "Dutch", "Nederlands", "5ddfbff0-7173-48ec-82e6-73343e7fce0f",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTg5OTAzMDA2YjA4YWI4OWQ2NjRiYTAyMzYxZTY3YzM4M2E1ZDEzYjdhNDY1YmY0Y2FiM2E4NDdkZTA0NDdhNCJ9fX0="),
	PL_PL("pl_PL", "Polish", "Polski", "2e4ea2c9-aa68-45a0-8a7e-8c5fbdfabeab",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDcwMGYwZGFmOTBiOTA3Mjc2OGZhODU4MGU4Y2IzOTE3ZGMzOTQwNDgyNWVjNWJmMjQ1YTA5ZjY5Yzg1ZTIwYyJ9fX0="),
	PT_BR("pt_BR", "Portuguese", "Português", "d7d1ff81-c959-4670-a0ea-a6220ee15640",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGY2YjU2NjViZWNjYmRhYWMxMDM4YWMxM2E5ZTA0YzE3ZjhmMTg0ZTEyMTYyZWQ4Y2Y5Nzk2NmQ5NjlhYzZmYiJ9fX0="),
	RU_RU("ru_RU", "Russian", "Русский", "849c610b-a1fe-48eb-840b-3fe775f6af6f",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmQ3MWUyMmM1OWQzODA5ZDI5NmQ4YWU0MWQ2OWVlMmI0NDc0MjUyY2NhNjg3YmI2YmUyNTM2NGQwNTU1NGI3MyJ9fX0="),
	SV_SE("sv_SE", "Swedish", "svenska", "af872887-7cc6-4935-9ab6-62c64283fd18",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjU2NWNkNDRjZGJkNDQxYTg4NGNjNTQyY2FlYjA2MzhhYzYyZWZmZjVlNjAwMjBhNWI0MDdlNzUzMDBhMmYxMCJ9fX0="),
	TR_TR("tr_TR", "Turkish", "Türkçe", "e831f1af-c758-4edc-b0fd-fb52dfb5d2d4",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTA5OTUyZGJmOTEzMWE2YTRmMWExYzk3NzBmOWQwODFhMjUxNGI5NjBjZjc1ZWQ1MmU0NzA0ZWMwMTI4ZDgyYiJ9fX0="),
	ZH_CN("zh_CN", "Chinese Simplified", "简体中文", "2ee56f96-3d1e-4d4d-a259-3828989023ce",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjY5MzlmYmEzYmQzNjU5MmExN2MzNTI5YTYwYThmYzgxY2U5OTk5N2VmZDI4MTU0MmMzZjhkNmRmZDljODIxMiJ9fX0="),
	ZH_TW("zh_TW", "Chinese Traditional", "繁體中文", "596bec47-3e6b-4325-b841-9ed36ac448a0",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTVkZDQ3NzcxNjY0ZjEzZTcxOGE3Zjc2Mjk4YzY3MmFhODMwNmQwMTM0ZTFhN2M5NTJmNDkwNjg3OTRlMjEwZSJ9fX0=");

	private final String langCode;
	private final String langNameInEnglish;
	private final String langNameInLang;
	private final String uuid;
	private final String texture;
	private static final Map<String, Language> BY_LANG_CODE = new HashMap<>();

	// Static block to populate the lookup map
	static {
		for (Language lang : values()) {
			BY_LANG_CODE.put(lang.langCode.toUpperCase(), lang);
		}
	}

	Language(String langCode, String langNameInEnglish, String langNameInLang, String uuid, String texture) {
		this.langCode = langCode;
		this.langNameInEnglish = langNameInEnglish;
		this.langNameInLang = langNameInLang;
		this.uuid = uuid;
		this.texture = texture;
	}

	public String getLangCode() {
		return langCode;
	}

	public String getLangNameInEnglish() {
		return langNameInEnglish;
	}

	public String getLangNameInLang() {
		return langNameInLang;
	}

	public String getUuid() {
		return uuid;
	}

	public String getTexture() {
		return texture;
	}

	// Lookup method to find Language by langCode
	public static Language getByLangCode(String langCode) {
		if (langCode == null) {
			return null;
		}
		return BY_LANG_CODE.get(langCode.toUpperCase());
	}
}