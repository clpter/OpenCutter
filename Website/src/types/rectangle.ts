import { SavedThickness } from './savedThickness';

export interface ListRectangeRow {
  id: string;
  width: string;
  height: string;
  count: string;
}

export interface InputRectangle {
  width: number;
  height: number;
  number: number;
  id: number;
}

export interface ApiRectangle {
  id: number;
  width: number;
  height: number;
  number: number;
  assignment: number;
  x: number;
  y: number;
}

export interface ApiResponse {
  stock: ApiRectangle[];
  placedPieces: ApiRectangle[];
  pieces: ApiRectangle[];
  waste: ApiRectangle[];
}

export interface CutOptions {
  addend: number;
  cuttingDirection: boolean;
  preferVerticalCuts: boolean;
  selectedValue: SavedThickness;
  thicknessDataList: SavedThickness[];
}
