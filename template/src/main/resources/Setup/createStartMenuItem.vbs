Set objFS = CreateObject("Scripting.FileSystemObject")
Set objShell = WScript.CreateObject("WScript.Shell")

Dim path
Dim lnk
Dim args
Dim desc

path = WScript.Arguments(0)
lnk = WScript.Arguments(1)
args = WScript.Arguments(2)
desc = WScript.Arguments(3)

WScript.StdOut.WriteLine("path: " & path)
WScript.StdOut.WriteLine("lnk: " & lnk)
WScript.StdOut.WriteLine("args: " & args)
WScript.StdOut.WriteLine("desc: " & desc)


InstallStartMenuLink path,lnk,args,desc

Function InstallStartMenuLink(path,lnk,args,desc)	
	strStartMenu = objShell.SpecialFolders("StartMenu" )
	menuArq = strStartMenu & "\" & path  '"\Programas\ARQ-SDK"
	newLnk = menuArq & "\"+desc+".lnk"
	If Not objFS.FolderExists(menuArq) Then
		objFS.CreateFolder(menuArq)
	ElseIf objFS.FileExists(newLnk) Then
		objFS.DeleteFile(newLnk)
	End If
	set oShellLink = objShell.CreateShortcut(newLnk)
	oShellLink.TargetPath = lnk
	oShellLink.Arguments = args
	oShellLink.WindowStyle = 1
	oShellLink.IconLocation = lnk
	oShellLink.Description = desc
	oShellLink.WorkingDirectory = objFS.GetParentFolderName(lnk)
	oShellLink.Save
End Function
