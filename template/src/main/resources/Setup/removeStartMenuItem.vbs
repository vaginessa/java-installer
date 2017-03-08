On Error Resume Next

Set objFS = CreateObject("Scripting.FileSystemObject")
Set objShell = WScript.CreateObject("WScript.Shell")

path = WScript.Arguments(0)

strStartMenu = objShell.SpecialFolders("StartMenu" )
If objFS.FolderExists(strStartMenu & "\" & path) Then
	objFS.DeleteFolder(strStartMenu & "\" & path)
End If
	
