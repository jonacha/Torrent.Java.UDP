package bitTorrent.metainfo;

import java.io.File;

import bitTorrent.metainfo.handler.MetainfoHandler;
import bitTorrent.metainfo.handler.MetainfoHandlerMultipleFile;
import bitTorrent.metainfo.handler.MetainfoHandlerSingleFile;

public class TorrentInfo {

	@SuppressWarnings("rawtypes")
	public MetainfoHandler extractInformationFromFile(File torrent) {
		try {
			MetainfoHandler<?> handler = null;
			try {
				if (torrent.getPath().contains(".torrent")) {
					handler = new MetainfoHandlerSingleFile();
					handler.parseTorrenFile(torrent.getPath());
					return handler;
				}
			} catch (Exception ex) {
				if (torrent.getPath().contains(".torrent")) {
					handler = new MetainfoHandlerMultipleFile();
					handler.parseTorrenFile(torrent.getPath());
					return handler;
				}
			}

			if (handler != null) {
				System.out.println(handler.getMetainfo());
			}
		} catch (Exception ex) {
			System.err.println("# TorrentInfoExtractor: " + ex.getMessage());
		}
		return null;

	}
}
