package ui.utils

import com.alibaba.fastjson2.toJSONString
import homework.grammatical.entity.SyntaxTree
import homework.grammatical.utils.outputSVGFile
import homework.grammatical.utils.toUmlTree
import homework.grammatical.语法分析器
import homework.lexical.codeStringToTokenList
import homework.lexical.entity.Category
import homework.quaternionTranslators.QuaternionGeneration
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File

fun outPutFile(file: File, outputPath: String) {
    val tokenJsStr = codeStringToTokenList(file.readText()).toJSONString()
    File("$outputPath/${file.nameWithoutExtension}.js").writeText(tokenJsStr)
    val expressionStore = 语法分析器(tokenJsStr)
    File("$outputPath/${file.nameWithoutExtension}_Expression.txt").writeText(expressionStore.toString())
    val syntaxTree = expressionStore.toTree().apply { tidy() }
    val umlTree = syntaxTree.toUmlTree()
    outputSVGFile(umlTree, "$outputPath/${file.nameWithoutExtension}.svg")
    val quaternionGeneration = QuaternionGeneration()
    quaternionGeneration.程序(syntaxTree.root)
    val sb = StringBuffer()
    sb.appendLine("四元式：${file.name}")
    quaternionGeneration.qExpressionList.forEach {
        sb.appendLine(it.toString())
    }
    sb.appendLine("函数表：")
    quaternionGeneration.functionList.forEach { function ->
        sb.appendLine(function.toString())
        function.parameterList.forEach { qExpression ->
            sb.appendLine(qExpression.toString())
        }
    }
    File("$outputPath/${file.nameWithoutExtension}_Quaternion.txt").writeText(sb.toString())
}

@OptIn(DelicateCoroutinesApi::class)
suspend fun outputFiles(files: List<File>, outputPath: String) {
    // 创建一个Channel，用于词法分析器和语法分析器之间的通信
    val fileChannel = Channel<File>()
    // 创建一个Channel，用于语法分析器与四元式生成器之间的通信
    val syntaxTreeChannel = Channel<SyntaxTree>()
    val directory = File(outputPath)
    if (!directory.isDirectory) throw Exception("此不是合法的文件夹")
    val errorList = mutableListOf<String>()
    val 词法分析 = GlobalScope.launch {
        files.forEach { file ->
            val content = File(file.path).readText()
            val tokens = try {
                codeStringToTokenList(content)
            } catch (e: Exception) {
                val errorName = "词法错误文件名: ${file.name}"
                errorList.add(errorName)
                val sb = StringBuffer()
                sb.appendLine(errorName)
                sb.appendLine(e.stackTraceToString())
                File("$outputPath/${file.nameWithoutExtension}_Error.txt").writeText(sb.toString())
                return@forEach
            }
            val jsStr = tokens.filter {
                !(it.category == Category.COMMENT || it.category == Category.ERROR)
            }.toJSONString()
            val outputFile = File("$outputPath/${file.nameWithoutExtension}.js")
            outputFile.writeText(jsStr)
            fileChannel.send(outputFile) // 发送文件
        }
        fileChannel.close() // 关闭文件Channel
    }
    val 语法分析 = GlobalScope.launch {
        for (file in fileChannel) {
            val content = File(file.path).readText()
            val expressionStore = try {
                语法分析器(content)
            } catch (e: Exception) {
                val errorName = "语法错误文件名: ${file.name}"
                errorList.add(errorName)
                val sb = StringBuffer()
                sb.appendLine(errorName)
                sb.appendLine(e.stackTraceToString())
                sb.appendLine()
                File("$outputPath/${file.nameWithoutExtension}_Error.txt").writeText(sb.toString())
                continue
            }
            val syntaxTree = expressionStore.toTree(name = file.nameWithoutExtension).apply { tidy() }
            syntaxTreeChannel.send(syntaxTree) // 发送语法树
            val umlTree = syntaxTree.toUmlTree("${file.nameWithoutExtension} 语法树")
            File("$outputPath/${file.nameWithoutExtension}_Expression.txt").writeText(expressionStore.toString())
            outputSVGFile(umlTree, "$outputPath/${file.nameWithoutExtension}.svg")
        }
        syntaxTreeChannel.close() // 关闭语法树Channel
    }
    val 四元式生成 = GlobalScope.launch {
        for (syntaxTree in syntaxTreeChannel) {
            val quaternionGeneration = QuaternionGeneration()
            try {
                quaternionGeneration.程序(syntaxTree.root)
            } catch (e: Exception) {
                val errorName = "四元式生成错误文件名: ${syntaxTree.name}.txt"
                errorList.add(errorName)
                val sb = StringBuffer()
                sb.appendLine(errorName)
                sb.appendLine(e.stackTraceToString())
                sb.appendLine()
                File("$outputPath/${syntaxTree.name}_Error_quaternionGeneration.txt").writeText(sb.toString())
                continue
            }
            val outputFile = File("$outputPath/${syntaxTree.name}_Quaternion.txt")
            val sb = StringBuffer()
            sb.append("main 函数四元式：\n")
            sb.append(quaternionGeneration.qExpressionList.joinToString("\n"))
            for (function in quaternionGeneration.functionList) {
                sb.append("\n\n\n${function.name} 函数参数列表：\n")
                sb.append(function.parameterList.joinToString("\t"))
                sb.append("\n${function.name} 函数四元式：\n")
                sb.append(function.qExpressionList.joinToString("\n"))
            }
            outputFile.writeText(sb.toString())
        }
    }

    // 依次执行协程
    runBlocking {
        词法分析.join()
        println("词法分析完成")
        语法分析.join()
        println("语法分析完成")
        四元式生成.join()
        println("四元式生成完成")
        println()
        if (errorList.isNotEmpty()) {
            val sb = StringBuffer()
            sb.append("总计出现${errorList.size}个错误：\n")
            errorList.forEach {
                if (it.endsWith(".sample")) sb.appendLine("词法错误: $it")
                else if (it.endsWith(".js")) sb.appendLine("语法错误: $it")
            }
            println(sb)
        }
    }
}