// Criador da fonte: Henrique de Souza Henrique
// Coautor e assistente de IA: Claude (Anthropic)
package com.hsh.meupdf

import android.content.Context
import android.os.Build
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter

/**
 * Registro de erros: se o app fechar, na próxima abertura ele mostra o que aconteceu (para você copiar e me mandar).
 * Erros "normais" (Java/Kotlin) ficam em ultimo_erro.txt; quedas nativas (que o Android não deixa capturar) são
 * detectadas por uma marca da operação que estava em andamento.
 */
object CrashLog {
    private fun arq(ctx: Context) = File(ctx.filesDir, "ultimo_erro.txt")
    private fun marca(ctx: Context) = File(ctx.filesDir, "operacao_em_andamento.txt")
    @Volatile private var contexto: Context? = null

    fun instalar(ctx: Context) {
        contexto = ctx.applicationContext
        val anterior = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            try {
                val sw = StringWriter()
                e.printStackTrace(PrintWriter(sw))
                arq(ctx).writeText(
                    "Versão 0.6 | Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT}) | ${Build.MANUFACTURER} ${Build.MODEL}\n" +
                        "Thread: ${t.name}\n\n$sw"
                )
            } catch (x: Exception) { }
            anterior?.uncaughtException(t, e)
        }
    }

    /** Anota a operação que vai começar; quem terminar bem chama [fim]. Se o app cair no meio, a marca fica. */
    fun inicio(acao: String) {
        val c = contexto ?: return
        try { marca(c).writeText(acao) } catch (e: Exception) { }
    }

    fun fim() {
        val c = contexto ?: return
        try { marca(c).delete() } catch (e: Exception) { }
    }

    /** Texto do que aconteceu na última vez, ou null se tudo correu bem. */
    fun relatorio(ctx: Context): String? {
        val partes = ArrayList<String>()
        try {
            if (arq(ctx).exists()) partes.add("ERRO:\n" + arq(ctx).readText())
            if (marca(ctx).exists()) partes.add("O app fechou de repente durante: " + marca(ctx).readText() + "\n(queda do componente nativo ou fechado pelo sistema)")
        } catch (e: Exception) { }
        return if (partes.isEmpty()) null else partes.joinToString("\n\n")
    }

    fun limpar(ctx: Context) {
        try { arq(ctx).delete(); marca(ctx).delete() } catch (e: Exception) { }
    }
}
