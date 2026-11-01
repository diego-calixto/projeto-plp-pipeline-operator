import { CodeCell } from "../models/cell/CodeCell";
import { MarkdownCell } from "../models/cell/MarkdownCell";
import { Notebook } from "../models/notebook/Notebook";
import { Workspace } from "../models/workspace/Workspace";
import { AVAILABLE_LANGUAGES } from "./languages";

function createInitialWorkspace() {
	const params = new URLSearchParams(window.location.search);
	const languageName = params.get("language");
	const code = params.get("code");
	const language = AVAILABLE_LANGUAGES.find(({ name }) => name === languageName);

	if (language && code) {
		return new Workspace(`Exemplo ${language.name}`, [
			new Notebook(language.name, language, [
				new MarkdownCell(`# Exemplo em ${language.name}`),
				new CodeCell(code),
			]),
		]);
	}

	return new Workspace("Untitled Workspace", [
		new Notebook("Notebook 1", AVAILABLE_LANGUAGES[0], [
			new MarkdownCell("# Welcome\nWrite notes and explanations here."),
			new CodeCell('length "abcd" + 6'),
		]),
	]);
}

export const INITIAL_WORKSPACE = createInitialWorkspace();
