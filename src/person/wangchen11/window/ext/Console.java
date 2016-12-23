package person.wangchen11.window.ext;

import jackpal.androidterm.TermFragment;
import jackpal.androidterm.emulatorview.TermSession;
import jackpal.androidterm.emulatorview.TermSession.FinishCallback;

import java.util.List;

import android.content.Context;
import android.support.v4.app.Fragment;
import person.wangchen11.console.ConsoleFragment;
import person.wangchen11.console.OnConsoleColseListener;
import person.wangchen11.process.ProcessState;
import person.wangchen11.window.MenuTag;
import person.wangchen11.window.Window;
import person.wangchen11.window.WindowPointer;
import person.wangchen11.window.WindowsManager;
import person.wangchen11.window.WindowsManager.WindowsManagerLintener;
import person.wangchen11.xqceditor.R;

public class Console implements Window, OnConsoleColseListener,WindowsManagerLintener, FinishCallback{
	static final String TAG="Console";
	private ConsoleFragment mConsoleFragment;
	private TermFragment mTermFragment;
	private WindowsManager mWindowsManager;
	private String mProcessName=null;
	public Console(WindowsManager windowsManager) {
		this(windowsManager,"");
	}
	
	public Console(WindowsManager windowsManager,String initCmd) {
		this(windowsManager,initCmd,true);
	}
	
	public Console(WindowsManager windowsManager,String initCmd,boolean needErrorInput) {
		this(windowsManager,initCmd,needErrorInput,false);
	}
	
	public Console(WindowsManager windowsManager,String initCmd,boolean needErrorInput,boolean runAsSu) {
		mWindowsManager=windowsManager;
		
		if(Setting.mConfig.mOtherConfig.mNewConsoleEnable)
		{
			mTermFragment=new TermFragment(initCmd, runAsSu);
			mTermFragment.setFinishCallback(this);
		}
		else
		{
			mConsoleFragment=new ConsoleFragment(initCmd,needErrorInput,runAsSu);
			mConsoleFragment.setConsoleCloseListener(this);
		}
		mWindowsManager.addListener(this);
	}
	
	public void setKillProcessName(String name)
	{
		mProcessName=name;
	}
	
	@Override
	public Fragment getFragment() {
		if(mConsoleFragment!=null)
			return mConsoleFragment;
		if(mTermFragment!=null)
			return mTermFragment;
		return null;
	}

	@Override
	public CharSequence getTitle(Context context) {
		return context.getText(R.string.console);
	}

	@Override
	public boolean onBackPressed() {
		return false;
	}

	@Override
	public boolean canAddNewWindow(Window window) {
		return true;
	}

	@Override
	public boolean onClose() {
		mWindowsManager.removeListener(this);
		if(mConsoleFragment!=null)
			mConsoleFragment.closeInputMethod();
		if(mTermFragment!=null)
			mTermFragment.destory();
		if(mProcessName!=null)
		{
			ProcessState state=ProcessState.getProcessByName(mProcessName);
			if(state!=null)
				state.kill();
		}
		return true;
	}

	@Override
	public List<MenuTag> getMenuTags() {
		return null;
	}

	@Override
	public boolean onMenuItemClick(int id) {
		return false;
	}

	@Override
	public void onConsoleClose(person.wangchen11.console.Console console) {
		mWindowsManager.closeWindow(this);
	}

	@Override
	public void onChangeWindow(WindowsManager manager) {
		if(mConsoleFragment!=null)
			mConsoleFragment.closeInputMethod();
	}

	@Override
	public void onAddWindow(WindowsManager manager, WindowPointer pointer) {
	}

	@Override
	public void onCloseWindow(WindowsManager manager, WindowPointer pointer) {
	}

	@Override
	public void onSessionFinish(TermSession session) {
		mWindowsManager.closeWindow(this);
	}
}
